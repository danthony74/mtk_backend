#!/usr/bin/env python3
"""
MTK Backend Terraform Deployment Script
A comprehensive deployment script that handles the complete deployment workflow.
"""

import argparse
import subprocess
import sys
import os
import json
import time
from pathlib import Path
from typing import List, Dict, Optional
import logging
from terraform_manager import TerraformManager

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('terraform_deploy.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class TerraformDeployer:
    def __init__(self, environment: str, terraform_dir: str = "iac"):
        self.environment = environment
        self.terraform_dir = terraform_dir
        self.tf_manager = TerraformManager(environment, terraform_dir)
        
    def check_prerequisites(self) -> bool:
        """Check if all prerequisites are met."""
        logger.info("Checking prerequisites...")
        
        # Check if terraform is installed
        try:
            result = subprocess.run(["terraform", "--version"], capture_output=True, text=True)
            if result.returncode != 0:
                logger.error("Terraform is not installed or not in PATH")
                return False
            logger.info(f"Terraform version: {result.stdout.split('\n')[0]}")
        except FileNotFoundError:
            logger.error("Terraform command not found")
            return False
        
        # Check if AWS CLI is configured
        try:
            result = subprocess.run(["aws", "sts", "get-caller-identity"], capture_output=True, text=True)
            if result.returncode != 0:
                logger.error("AWS CLI is not configured or credentials are invalid")
                return False
            identity = json.loads(result.stdout)
            logger.info(f"AWS Account: {identity['Account']}, User: {identity['Arn']}")
        except (FileNotFoundError, json.JSONDecodeError):
            logger.error("AWS CLI is not installed or not configured")
            return False
        
        # Check if required AWS services are available
        required_services = ["ec2", "rds", "dynamodb", "iam", "cloudwatch"]
        for service in required_services:
            try:
                if service == "ec2":
                    subprocess.run(["aws", "ec2", "describe-regions"], capture_output=True, check=True)
                elif service == "rds":
                    subprocess.run(["aws", "rds", "describe-db-engine-versions", "--engine", "aurora-postgresql"], capture_output=True, check=True)
                elif service == "dynamodb":
                    subprocess.run(["aws", "dynamodb", "list-tables"], capture_output=True, check=True)
                elif service == "iam":
                    subprocess.run(["aws", "iam", "get-user"], capture_output=True, check=True)
                elif service == "cloudwatch":
                    subprocess.run(["aws", "cloudwatch", "list-metrics"], capture_output=True, check=True)
                logger.info(f"AWS {service.upper()} service is accessible")
            except subprocess.CalledProcessError:
                logger.error(f"AWS {service.upper()} service is not accessible")
                return False
        
        logger.info("All prerequisites are met")
        return True
    
    def validate_configuration(self) -> bool:
        """Validate Terraform configuration."""
        logger.info("Validating Terraform configuration...")
        
        # Initialize Terraform
        if not self.tf_manager.init():
            logger.error("Failed to initialize Terraform")
            return False
        
        # Validate configuration
        if not self.tf_manager.validate():
            logger.error("Terraform configuration validation failed")
            return False
        
        logger.info("Terraform configuration is valid")
        return True
    
    def create_plan(self, detailed: bool = False) -> bool:
        """Create and review Terraform plan."""
        logger.info("Creating Terraform plan...")
        
        if not self.tf_manager.plan(detailed=detailed):
            logger.error("Failed to create Terraform plan")
            return False
        
        logger.info("Terraform plan created successfully")
        return True
    
    def apply_changes(self, auto_approve: bool = False) -> bool:
        """Apply Terraform changes."""
        logger.info("Applying Terraform changes...")
        
        if not self.tf_manager.apply(auto_approve=auto_approve):
            logger.error("Failed to apply Terraform changes")
            return False
        
        logger.info("Terraform changes applied successfully")
        return True
    
    def verify_deployment(self) -> bool:
        """Verify that the deployment was successful."""
        logger.info("Verifying deployment...")
        
        # Get outputs
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.error("Failed to get Terraform outputs")
            return False
        
        # Check if key resources exist
        try:
            # Check VPC
            if 'vpc_id' in outputs:
                vpc_id = outputs['vpc_id']['value']
                result = subprocess.run(
                    ["aws", "ec2", "describe-vpcs", "--vpc-ids", vpc_id],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"VPC {vpc_id} not found")
                    return False
                logger.info(f"VPC {vpc_id} verified")
            
            # Check Aurora cluster
            if 'aurora_cluster_id' in outputs:
                cluster_id = outputs['aurora_cluster_id']['value']
                result = subprocess.run(
                    ["aws", "rds", "describe-db-clusters", "--db-cluster-identifier", cluster_id],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"Aurora cluster {cluster_id} not found")
                    return False
                logger.info(f"Aurora cluster {cluster_id} verified")
            
            # Check DynamoDB table
            if 'dynamodb_table_name' in outputs:
                table_name = outputs['dynamodb_table_name']['value']
                result = subprocess.run(
                    ["aws", "dynamodb", "describe-table", "--table-name", table_name],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"DynamoDB table {table_name} not found")
                    return False
                logger.info(f"DynamoDB table {table_name} verified")
            
        except Exception as e:
            logger.error(f"Error during deployment verification: {e}")
            return False
        
        logger.info("Deployment verification completed successfully")
        return True
    
    def deploy(self, auto_approve: bool = False, detailed: bool = False) -> bool:
        """Complete deployment workflow."""
        logger.info(f"Starting deployment for environment: {self.environment}")
        
        # Step 1: Check prerequisites
        if not self.check_prerequisites():
            logger.error("Prerequisites check failed")
            return False
        
        # Step 2: Validate configuration
        if not self.validate_configuration():
            logger.error("Configuration validation failed")
            return False
        
        # Step 3: Create plan
        if not self.create_plan(detailed=detailed):
            logger.error("Plan creation failed")
            return False
        
        # Step 4: Apply changes
        if not self.apply_changes(auto_approve=auto_approve):
            logger.error("Apply failed")
            return False
        
        # Step 5: Verify deployment
        if not self.verify_deployment():
            logger.error("Deployment verification failed")
            return False
        
        logger.info(f"Deployment completed successfully for environment: {self.environment}")
        return True
    
    def destroy_infrastructure(self, auto_approve: bool = False) -> bool:
        """Destroy infrastructure."""
        logger.warning(f"Starting infrastructure destruction for environment: {self.environment}")
        
        # Check prerequisites
        if not self.check_prerequisites():
            logger.error("Prerequisites check failed")
            return False
        
        # Initialize Terraform
        if not self.tf_manager.init():
            logger.error("Failed to initialize Terraform")
            return False
        
        # Destroy infrastructure
        if not self.tf_manager.destroy(auto_approve=auto_approve):
            logger.error("Failed to destroy infrastructure")
            return False
        
        logger.info(f"Infrastructure destruction completed for environment: {self.environment}")
        return True
    
    def get_deployment_info(self) -> Optional[Dict]:
        """Get deployment information."""
        logger.info("Getting deployment information...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.error("Failed to get deployment information")
            return None
        
        # Format the information
        deployment_info = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "resources": {}
        }
        
        # Add resource information
        for key, value in outputs.items():
            if isinstance(value, dict) and 'value' in value:
                deployment_info["resources"][key] = value['value']
        
        return deployment_info

def main():
    parser = argparse.ArgumentParser(
        description="MTK Backend Terraform Deployment Script",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python terraform_deploy.py dev deploy
  python terraform_deploy.py qa deploy --auto-approve
  python terraform_deploy.py prod deploy --detailed
  python terraform_deploy.py dev destroy --auto-approve
  python terraform_deploy.py qa info
        """
    )
    
    parser.add_argument(
        "environment",
        choices=["dev", "qa", "prod"],
        help="Environment to work with"
    )
    
    parser.add_argument(
        "action",
        choices=["deploy", "destroy", "info", "validate", "plan"],
        help="Action to perform"
    )
    
    parser.add_argument(
        "--terraform-dir",
        default="iac",
        help="Directory containing Terraform files (default: iac)"
    )
    
    parser.add_argument(
        "--auto-approve",
        action="store_true",
        help="Skip interactive approval"
    )
    
    parser.add_argument(
        "--detailed",
        action="store_true",
        help="Show detailed output"
    )
    
    args = parser.parse_args()
    
    try:
        # Initialize deployer
        deployer = TerraformDeployer(args.environment, args.terraform_dir)
        
        # Perform the requested action
        success = False
        
        if args.action == "deploy":
            success = deployer.deploy(auto_approve=args.auto_approve, detailed=args.detailed)
        
        elif args.action == "destroy":
            success = deployer.destroy_infrastructure(auto_approve=args.auto_approve)
        
        elif args.action == "info":
            info = deployer.get_deployment_info()
            if info:
                print(json.dumps(info, indent=2))
                success = True
            else:
                success = False
        
        elif args.action == "validate":
            success = deployer.validate_configuration()
        
        elif args.action == "plan":
            success = deployer.create_plan(detailed=args.detailed)
        
        # Exit with appropriate code
        sys.exit(0 if success else 1)
    
    except Exception as e:
        logger.error(f"Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 