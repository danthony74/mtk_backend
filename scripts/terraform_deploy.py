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
    def __init__(self, environment: str, terraform_dir: str = "iac", tf_file: str = None):
        self.environment = environment
        self.terraform_dir = terraform_dir
        self.tf_file = tf_file
        self.tf_manager = TerraformManager(environment, terraform_dir, tf_file)
        
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
        required_services = ["ec2", "rds", "dynamodb", "iam", "cloudwatch", "apigateway", "cognito-idp", "wafv2", "lambda"]
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
                elif service == "apigateway":
                    subprocess.run(["aws", "apigateway", "get-rest-apis"], capture_output=True, check=True)
                elif service == "cognito-idp":
                    subprocess.run(["aws", "cognito-idp", "list-user-pools", "--max-items", "1"], capture_output=True, check=True)
                elif service == "wafv2":
                    subprocess.run(["aws", "wafv2", "list-web-acls", "--scope", "REGIONAL"], capture_output=True, check=True)
                elif service == "lambda":
                    subprocess.run(["aws", "lambda", "list-functions", "--max-items", "1"], capture_output=True, check=True)
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
            
            # Check API Gateway
            if 'api_gateway_id' in outputs:
                api_id = outputs['api_gateway_id']['value']
                result = subprocess.run(
                    ["aws", "apigateway", "get-rest-api", "--rest-api-id", api_id],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"API Gateway {api_id} not found")
                    return False
                logger.info(f"API Gateway {api_id} verified")
            
            # Check Cognito User Pool
            if 'cognito_user_pool_id' in outputs:
                user_pool_id = outputs['cognito_user_pool_id']['value']
                result = subprocess.run(
                    ["aws", "cognito-idp", "describe-user-pool", "--user-pool-id", user_pool_id],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"Cognito User Pool {user_pool_id} not found")
                    return False
                logger.info(f"Cognito User Pool {user_pool_id} verified")
            
            # Check Lambda functions
            if 'lambda_function_names' in outputs:
                function_names = outputs['lambda_function_names']['value']
                for function_name in function_names:
                    result = subprocess.run(
                        ["aws", "lambda", "get-function", "--function-name", function_name],
                        capture_output=True, text=True
                    )
                    if result.returncode != 0:
                        logger.error(f"Lambda function {function_name} not found")
                        return False
                    logger.info(f"Lambda function {function_name} verified")
            
            logger.info("All resources verified successfully")
            return True
            
        except Exception as e:
            logger.error(f"Error during verification: {e}")
            return False
    
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
        """Destroy the infrastructure."""
        logger.warning(f"Destroying infrastructure for environment: {self.environment}")
        
        if not self.tf_manager.destroy(auto_approve=auto_approve):
            logger.error("Failed to destroy infrastructure")
            return False
        
        logger.info(f"Infrastructure destroyed successfully for environment: {self.environment}")
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
        if 'vpc_id' in outputs:
            deployment_info["resources"]["vpc"] = outputs['vpc_id']['value']
        if 'aurora_cluster_id' in outputs:
            deployment_info["resources"]["aurora_cluster"] = outputs['aurora_cluster_id']['value']
        if 'dynamodb_table_name' in outputs:
            deployment_info["resources"]["dynamodb_table"] = outputs['dynamodb_table_name']['value']
        if 'api_gateway_id' in outputs:
            deployment_info["resources"]["api_gateway"] = outputs['api_gateway_id']['value']
        if 'cognito_user_pool_id' in outputs:
            deployment_info["resources"]["cognito_user_pool"] = outputs['cognito_user_pool_id']['value']
        if 'lambda_function_names' in outputs:
            deployment_info["resources"]["lambda_functions"] = outputs['lambda_function_names']['value']
        
        return deployment_info

def main():
    parser = argparse.ArgumentParser(description="MTK Backend Terraform Deployment Script")
    parser.add_argument("environment", choices=["dev", "qa", "prod"], help="Environment to deploy")
    parser.add_argument("--terraform-dir", default="iac", help="Terraform directory path")
    parser.add_argument("--tf-file", help="Specific Terraform file to target")
    parser.add_argument("--auto-approve", action="store_true", help="Auto-approve changes")
    parser.add_argument("--detailed", action="store_true", help="Show detailed plan output")
    parser.add_argument("--destroy", action="store_true", help="Destroy infrastructure")
    parser.add_argument("--info", action="store_true", help="Get deployment information")
    
    args = parser.parse_args()
    
    try:
        deployer = TerraformDeployer(args.environment, args.terraform_dir, args.tf_file)
        
        if args.destroy:
            success = deployer.destroy_infrastructure(args.auto_approve)
        elif args.info:
            info = deployer.get_deployment_info()
            if info:
                print(json.dumps(info, indent=2))
            else:
                sys.exit(1)
        else:
            success = deployer.deploy(args.auto_approve, args.detailed)
        
        if not success:
            sys.exit(1)
            
    except Exception as e:
        logger.error(f"Deployment failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 