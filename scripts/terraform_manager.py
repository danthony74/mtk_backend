#!/usr/bin/env python3
"""
MTK Backend Terraform Manager
A Python script to manage Terraform infrastructure for different environments.
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

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('terraform_manager.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class TerraformManager:
    def __init__(self, environment: str, terraform_dir: str = "iac", tf_file: str = None):
        self.environment = environment
        self.terraform_dir = Path(terraform_dir)
        self.var_file = f"terraform.tfvars.{environment}"
        self.state_file = f"terraform.tfstate.{environment}"
        self.tf_file = tf_file
        
        # Validate environment
        valid_environments = ["dev", "qa", "prod"]
        if environment not in valid_environments:
            raise ValueError(f"Environment must be one of: {valid_environments}")
        
        # Check if terraform directory exists
        if not self.terraform_dir.exists():
            raise FileNotFoundError(f"Terraform directory not found: {self.terraform_dir}")
        
        # Check if var file exists
        var_file_path = self.terraform_dir / self.var_file
        if not var_file_path.exists():
            raise FileNotFoundError(f"Variable file not found: {var_file_path}")
        
        # Validate tf_file if specified
        if tf_file:
            valid_files = ["main", "network", "database", "dynamodb", "lambda", "apigateway"]
            if tf_file not in valid_files:
                raise ValueError(f"Terraform file must be one of: {valid_files}")
            
            # Check if the specified file exists
            tf_file_path = self.terraform_dir / f"{tf_file}.tf"
            if not tf_file_path.exists():
                raise FileNotFoundError(f"Terraform file not found: {tf_file_path}")
        
        logger.info(f"Initialized TerraformManager for environment: {environment}" + (f", file: {tf_file}" if tf_file else ""))
    
    def _run_command(self, command: List[str], capture_output: bool = True) -> Dict:
        """Run a command and return the result."""
        try:
            logger.info(f"Running command: {' '.join(command)}")
            
            if capture_output:
                result = subprocess.run(
                    command,
                    cwd=self.terraform_dir,
                    capture_output=True,
                    text=True,
                    timeout=300  # 5 minute timeout
                )
            else:
                result = subprocess.run(
                    command,
                    cwd=self.terraform_dir,
                    timeout=300
                )
            
            return {
                'success': result.returncode == 0,
                'returncode': result.returncode,
                'stdout': result.stdout if capture_output else None,
                'stderr': result.stderr if capture_output else None
            }
        
        except subprocess.TimeoutExpired:
            logger.error("Command timed out after 5 minutes")
            return {'success': False, 'returncode': -1, 'stdout': None, 'stderr': 'Command timed out'}
        except Exception as e:
            logger.error(f"Error running command: {e}")
            return {'success': False, 'returncode': -1, 'stdout': None, 'stderr': str(e)}
    
    def init(self) -> bool:
        """Initialize Terraform."""
        logger.info("Initializing Terraform...")
        result = self._run_command(["terraform", "init"])
        
        if result['success']:
            logger.info("Terraform initialized successfully")
        else:
            logger.error(f"Terraform init failed: {result['stderr']}")
        
        return result['success']
    
    def validate(self) -> bool:
        """Validate Terraform configuration."""
        logger.info("Validating Terraform configuration...")
        result = self._run_command(["terraform", "validate"])
        
        if result['success']:
            logger.info("Terraform configuration is valid")
        else:
            logger.error(f"Terraform validation failed: {result['stderr']}")
        
        return result['success']
    
    def plan(self, detailed: bool = False) -> bool:
        """Create a Terraform plan."""
        logger.info(f"Creating Terraform plan for {self.environment}...")
        
        command = ["terraform", "plan", f"-var-file={self.var_file}"]
        
        if self.tf_file:
            command.extend(["-target", f"module.{self.tf_file}"])
            logger.info(f"Targeting specific module: {self.tf_file}")
        
        if detailed:
            command.append("-detailed-exitcode")
        
        result = self._run_command(command)
        
        if result['success']:
            logger.info("Terraform plan created successfully")
            if detailed and result['stdout']:
                print(result['stdout'])
        else:
            logger.error(f"Terraform plan failed: {result['stderr']}")
        
        return result['success']
    
    def apply(self, auto_approve: bool = False) -> bool:
        """Apply Terraform configuration."""
        logger.info(f"Applying Terraform configuration for {self.environment}...")
        
        command = ["terraform", "apply", f"-var-file={self.var_file}"]
        
        if self.tf_file:
            command.extend(["-target", f"module.{self.tf_file}"])
            logger.info(f"Targeting specific module: {self.tf_file}")
        
        if auto_approve:
            command.append("-auto-approve")
        
        result = self._run_command(command, capture_output=not auto_approve)
        
        if result['success']:
            logger.info("Terraform apply completed successfully")
        else:
            logger.error(f"Terraform apply failed: {result['stderr']}")
        
        return result['success']
    
    def destroy(self, auto_approve: bool = False) -> bool:
        """Destroy Terraform infrastructure."""
        logger.warning(f"Destroying Terraform infrastructure for {self.environment}...")
        
        command = ["terraform", "destroy", f"-var-file={self.var_file}"]
        
        if self.tf_file:
            command.extend(["-target", f"module.{self.tf_file}"])
            logger.info(f"Targeting specific module: {self.tf_file}")
        
        if auto_approve:
            command.append("-auto-approve")
        
        result = self._run_command(command, capture_output=not auto_approve)
        
        if result['success']:
            logger.info("Terraform destroy completed successfully")
        else:
            logger.error(f"Terraform destroy failed: {result['stderr']}")
        
        return result['success']
    
    def get_state(self) -> Optional[Dict]:
        """Get current Terraform state."""
        logger.info("Getting Terraform state...")
        result = self._run_command(["terraform", "show", "-json"])
        
        if result['success'] and result['stdout']:
            try:
                state = json.loads(result['stdout'])
                logger.info("Terraform state retrieved successfully")
                return state
            except json.JSONDecodeError as e:
                logger.error(f"Failed to parse Terraform state JSON: {e}")
                return None
        else:
            logger.error(f"Failed to get Terraform state: {result['stderr']}")
            return None
    
    def get_outputs(self) -> Optional[Dict]:
        """Get Terraform outputs."""
        logger.info("Getting Terraform outputs...")
        result = self._run_command(["terraform", "output", "-json"])
        
        if result['success'] and result['stdout']:
            try:
                outputs = json.loads(result['stdout'])
                logger.info("Terraform outputs retrieved successfully")
                return outputs
            except json.JSONDecodeError as e:
                logger.error(f"Failed to parse Terraform outputs JSON: {e}")
                return None
        else:
            logger.error(f"Failed to get Terraform outputs: {result['stderr']}")
            return None
    
    def refresh(self) -> bool:
        """Refresh Terraform state."""
        logger.info("Refreshing Terraform state...")
        result = self._run_command(["terraform", "refresh", f"-var-file={self.var_file}"])
        
        if result['success']:
            logger.info("Terraform state refreshed successfully")
        else:
            logger.error(f"Terraform refresh failed: {result['stderr']}")
        
        return result['success']
    
    def import_resource(self, resource_address: str, resource_id: str) -> bool:
        """Import an existing resource into Terraform state."""
        logger.info(f"Importing resource {resource_address} with ID {resource_id}...")
        command = ["terraform", "import", resource_address, resource_id]
        result = self._run_command(command)
        
        if result['success']:
            logger.info(f"Resource {resource_address} imported successfully")
        else:
            logger.error(f"Resource import failed: {result['stderr']}")
        
        return result['success']
    
    def workspace_list(self) -> List[str]:
        """List available Terraform workspaces."""
        logger.info("Listing Terraform workspaces...")
        result = self._run_command(["terraform", "workspace", "list"])
        
        if result['success'] and result['stdout']:
            workspaces = [ws.strip() for ws in result['stdout'].split('\n') if ws.strip()]
            logger.info(f"Found workspaces: {workspaces}")
            return workspaces
        else:
            logger.error(f"Failed to list workspaces: {result['stderr']}")
            return []
    
    def workspace_select(self, workspace: str) -> bool:
        """Select a Terraform workspace."""
        logger.info(f"Selecting Terraform workspace: {workspace}")
        result = self._run_command(["terraform", "workspace", "select", workspace])
        
        if result['success']:
            logger.info(f"Workspace {workspace} selected successfully")
        else:
            logger.error(f"Failed to select workspace {workspace}: {result['stderr']}")
        
        return result['success']
    
    def workspace_new(self, workspace: str) -> bool:
        """Create a new Terraform workspace."""
        logger.info(f"Creating new Terraform workspace: {workspace}")
        result = self._run_command(["terraform", "workspace", "new", workspace])
        
        if result['success']:
            logger.info(f"Workspace {workspace} created successfully")
        else:
            logger.error(f"Failed to create workspace {workspace}: {result['stderr']}")
        
        return result['success']

def main():
    parser = argparse.ArgumentParser(description="MTK Backend Terraform Manager")
    parser.add_argument("environment", choices=["dev", "qa", "prod"], help="Environment to work with")
    parser.add_argument("action", choices=["init", "validate", "plan", "apply", "destroy", "refresh", "output", "state", "workspace"], help="Action to perform")
    parser.add_argument("--terraform-dir", default="iac", help="Terraform directory path")
    parser.add_argument("--tf-file", help="Specific Terraform file to target")
    parser.add_argument("--auto-approve", action="store_true", help="Auto-approve changes")
    parser.add_argument("--detailed", action="store_true", help="Show detailed output")
    parser.add_argument("--workspace", help="Workspace name for workspace actions")
    parser.add_argument("--resource-address", help="Resource address for import")
    parser.add_argument("--resource-id", help="Resource ID for import")
    
    args = parser.parse_args()
    
    try:
        tf_manager = TerraformManager(args.environment, args.terraform_dir, args.tf_file)
        
        success = False
        
        if args.action == "init":
            success = tf_manager.init()
        elif args.action == "validate":
            success = tf_manager.validate()
        elif args.action == "plan":
            success = tf_manager.plan(detailed=args.detailed)
        elif args.action == "apply":
            success = tf_manager.apply(auto_approve=args.auto_approve)
        elif args.action == "destroy":
            success = tf_manager.destroy(auto_approve=args.auto_approve)
        elif args.action == "refresh":
            success = tf_manager.refresh()
        elif args.action == "output":
            outputs = tf_manager.get_outputs()
            if outputs:
                print(json.dumps(outputs, indent=2))
                success = True
            else:
                success = False
        elif args.action == "state":
            state = tf_manager.get_state()
            if state:
                print(json.dumps(state, indent=2))
                success = True
            else:
                success = False
        elif args.action == "workspace":
            if args.workspace:
                success = tf_manager.workspace_select(args.workspace)
            else:
                workspaces = tf_manager.workspace_list()
                print(json.dumps(workspaces, indent=2))
                success = True
        
        if not success:
            sys.exit(1)
            
    except Exception as e:
        logger.error(f"Terraform manager failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 