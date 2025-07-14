#!/usr/bin/env python3
"""
MTK Backend API Gateway Deployment Script
Specialized deployment script for API Gateway with WAF and Cognito integration.
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
        logging.FileHandler('apigateway_deploy.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class APIGatewayDeployer:
    def __init__(self, environment: str, terraform_dir: str = "iac"):
        self.environment = environment
        self.terraform_dir = terraform_dir
        self.tf_manager = TerraformManager(environment, terraform_dir)
        
    def check_api_gateway_prerequisites(self) -> bool:
        """Check API Gateway specific prerequisites."""
        logger.info("Checking API Gateway prerequisites...")
        
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
        
        # Check API Gateway specific services
        required_services = ["apigateway", "cognito-idp", "wafv2", "iam", "cloudwatch"]
        for service in required_services:
            try:
                if service == "apigateway":
                    subprocess.run(["aws", "apigateway", "get-rest-apis"], capture_output=True, check=True)
                elif service == "cognito-idp":
                    subprocess.run(["aws", "cognito-idp", "list-user-pools", "--max-items", "1"], capture_output=True, check=True)
                elif service == "wafv2":
                    subprocess.run(["aws", "wafv2", "list-web-acls", "--scope", "REGIONAL"], capture_output=True, check=True)
                elif service == "iam":
                    subprocess.run(["aws", "iam", "get-user"], capture_output=True, check=True)
                elif service == "cloudwatch":
                    subprocess.run(["aws", "cloudwatch", "list-metrics"], capture_output=True, check=True)
                logger.info(f"AWS {service.upper()} service is accessible")
            except subprocess.CalledProcessError:
                logger.error(f"AWS {service.upper()} service is not accessible")
                return False
        
        logger.info("API Gateway prerequisites are met")
        return True
    
    def check_network_dependencies(self) -> bool:
        """Check if network dependencies are available."""
        logger.info("Checking network dependencies...")
        
        # Get current outputs to check for network resources
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.warning("No Terraform outputs found. Network dependencies may not be available.")
            return True  # Continue anyway, as this might be initial deployment
        
        # Check if VPC exists (network module should be deployed first)
        if 'vpc_id' in outputs:
            vpc_id = outputs['vpc_id']['value']
            try:
                result = subprocess.run(
                    ["aws", "ec2", "describe-vpcs", "--vpc-ids", vpc_id],
                    capture_output=True, text=True
                )
                if result.returncode == 0:
                    logger.info(f"VPC {vpc_id} is available")
                    return True
                else:
                    logger.warning(f"VPC {vpc_id} not found. Network module may need to be deployed first.")
                    return False
            except Exception as e:
                logger.warning(f"Could not verify VPC: {e}")
                return True  # Continue anyway
        
        logger.warning("No VPC found in outputs. Network module may need to be deployed first.")
        return True  # Continue anyway
    
    def deploy_api_gateway(self, auto_approve: bool = False, detailed: bool = False) -> bool:
        """Deploy API Gateway with all its components."""
        logger.info(f"Starting API Gateway deployment for environment: {self.environment}")
        
        # Step 1: Check prerequisites
        if not self.check_api_gateway_prerequisites():
            logger.error("API Gateway prerequisites check failed")
            return False
        
        # Step 2: Check network dependencies
        if not self.check_network_dependencies():
            logger.warning("Network dependencies not available. Consider deploying network module first.")
            # Continue anyway as API Gateway can be deployed independently
        
        # Step 3: Initialize Terraform
        if not self.tf_manager.init():
            logger.error("Failed to initialize Terraform")
            return False
        
        # Step 4: Validate configuration
        if not self.tf_manager.validate():
            logger.error("Terraform configuration validation failed")
            return False
        
        # Step 5: Create plan for API Gateway module
        logger.info("Creating API Gateway deployment plan...")
        command = ["terraform", "plan", f"-var-file=terraform.tfvars.{self.environment}", "-target=module.apigateway"]
        
        if detailed:
            command.append("-detailed-exitcode")
        
        result = self.tf_manager._run_command(command)
        
        if not result['success']:
            logger.error(f"API Gateway plan failed: {result['stderr']}")
            return False
        
        logger.info("API Gateway plan created successfully")
        if detailed and result['stdout']:
            print(result['stdout'])
        
        # Step 6: Apply API Gateway changes
        logger.info("Applying API Gateway changes...")
        command = ["terraform", "apply", f"-var-file=terraform.tfvars.{self.environment}", "-target=module.apigateway"]
        
        if auto_approve:
            command.append("-auto-approve")
        
        result = self.tf_manager._run_command(command, capture_output=not auto_approve)
        
        if not result['success']:
            logger.error(f"API Gateway apply failed: {result['stderr']}")
            return False
        
        logger.info("API Gateway changes applied successfully")
        
        # Step 7: Verify API Gateway deployment
        if not self.verify_api_gateway_deployment():
            logger.error("API Gateway deployment verification failed")
            return False
        
        logger.info(f"API Gateway deployment completed successfully for environment: {self.environment}")
        return True
    
    def verify_api_gateway_deployment(self) -> bool:
        """Verify API Gateway deployment."""
        logger.info("Verifying API Gateway deployment...")
        
        # Get outputs
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.error("Failed to get Terraform outputs")
            return False
        
        try:
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
            
            # Check WAF Web ACL
            if 'waf_web_acl_id' in outputs:
                web_acl_id = outputs['waf_web_acl_id']['value']
                result = subprocess.run(
                    ["aws", "wafv2", "get-web-acl", "--id", web_acl_id, "--scope", "REGIONAL"],
                    capture_output=True, text=True
                )
                if result.returncode != 0:
                    logger.error(f"WAF Web ACL {web_acl_id} not found")
                    return False
                logger.info(f"WAF Web ACL {web_acl_id} verified")
            
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
            
            logger.info("API Gateway deployment verification completed successfully")
            return True
            
        except Exception as e:
            logger.error(f"Error during API Gateway verification: {e}")
            return False
    
    def get_api_gateway_info(self) -> Optional[Dict]:
        """Get API Gateway deployment information."""
        logger.info("Getting API Gateway deployment information...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.error("Failed to get Terraform outputs")
            return None
        
        # Format the information
        api_info = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "api_gateway": {},
            "cognito": {},
            "waf": {},
            "lambda": {}
        }
        
        # Add API Gateway information
        if 'api_gateway_id' in outputs:
            api_info["api_gateway"]["id"] = outputs['api_gateway_id']['value']
        if 'api_gateway_url' in outputs:
            api_info["api_gateway"]["url"] = outputs['api_gateway_url']['value']
        if 'api_gateway_name' in outputs:
            api_info["api_gateway"]["name"] = outputs['api_gateway_name']['value']
        
        # Add Cognito information
        if 'cognito_user_pool_id' in outputs:
            api_info["cognito"]["user_pool_id"] = outputs['cognito_user_pool_id']['value']
        if 'cognito_user_pool_client_id' in outputs:
            api_info["cognito"]["client_id"] = outputs['cognito_user_pool_client_id']['value']
        if 'cognito_domain' in outputs:
            api_info["cognito"]["domain"] = outputs['cognito_domain']['value']
        
        # Add WAF information
        if 'waf_web_acl_id' in outputs:
            api_info["waf"]["web_acl_id"] = outputs['waf_web_acl_id']['value']
        if 'waf_web_acl_name' in outputs:
            api_info["waf"]["name"] = outputs['waf_web_acl_name']['value']
        
        # Add Lambda information
        if 'lambda_function_names' in outputs:
            api_info["lambda"]["functions"] = outputs['lambda_function_names']['value']
        
        return api_info
    
    def test_api_gateway_endpoint(self) -> bool:
        """Test API Gateway endpoint."""
        logger.info("Testing API Gateway endpoint...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs or 'api_gateway_url' not in outputs:
            logger.error("API Gateway URL not found in outputs")
            return False
        
        api_url = outputs['api_gateway_url']['value']
        
        try:
            # Test the health check endpoint
            import requests
            
            health_url = f"{api_url}/health"
            response = requests.get(health_url, timeout=10)
            
            if response.status_code == 200:
                logger.info(f"API Gateway endpoint test successful: {health_url}")
                return True
            else:
                logger.warning(f"API Gateway endpoint test returned status {response.status_code}: {health_url}")
                return False
                
        except Exception as e:
            logger.error(f"API Gateway endpoint test failed: {e}")
            return False

def main():
    parser = argparse.ArgumentParser(description="MTK Backend API Gateway Deployment Script")
    parser.add_argument("environment", choices=["dev", "qa", "prod"], help="Environment to deploy")
    parser.add_argument("--terraform-dir", default="iac", help="Terraform directory path")
    parser.add_argument("--auto-approve", action="store_true", help="Auto-approve changes")
    parser.add_argument("--detailed", action="store_true", help="Show detailed plan output")
    parser.add_argument("--info", action="store_true", help="Get API Gateway information")
    parser.add_argument("--test", action="store_true", help="Test API Gateway endpoint")
    
    args = parser.parse_args()
    
    try:
        deployer = APIGatewayDeployer(args.environment, args.terraform_dir)
        
        if args.info:
            info = deployer.get_api_gateway_info()
            if info:
                print(json.dumps(info, indent=2))
            else:
                sys.exit(1)
        elif args.test:
            success = deployer.test_api_gateway_endpoint()
            if not success:
                sys.exit(1)
        else:
            success = deployer.deploy_api_gateway(args.auto_approve, args.detailed)
            if not success:
                sys.exit(1)
                
    except Exception as e:
        logger.error(f"API Gateway deployment failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 