#!/usr/bin/env python3
"""
MTK Backend Terraform Monitoring Script
Monitor the health and status of deployed infrastructure.
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
        logging.FileHandler('terraform_monitor.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class TerraformMonitor:
    def __init__(self, environment: str, terraform_dir: str = "iac"):
        self.environment = environment
        self.terraform_dir = terraform_dir
        self.tf_manager = TerraformManager(environment, terraform_dir)
        
    def get_resource_status(self) -> Dict:
        """Get the status of all deployed resources."""
        logger.info("Getting resource status...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            logger.error("Failed to get Terraform outputs")
            return {}
        
        status = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "resources": {}
        }
        
        try:
            # Check VPC status
            if 'vpc_id' in outputs:
                vpc_id = outputs['vpc_id']['value']
                result = subprocess.run(
                    ["aws", "ec2", "describe-vpcs", "--vpc-ids", vpc_id],
                    capture_output=True, text=True
                )
                if result.returncode == 0:
                    vpc_data = json.loads(result.stdout)
                    vpc = vpc_data['Vpcs'][0]
                    status["resources"]["vpc"] = {
                        "id": vpc_id,
                        "state": vpc['State'],
                        "cidr": vpc['CidrBlock'],
                        "healthy": vpc['State'] == 'available'
                    }
            
            # Check Aurora cluster status
            if 'aurora_cluster_id' in outputs:
                cluster_id = outputs['aurora_cluster_id']['value']
                result = subprocess.run(
                    ["aws", "rds", "describe-db-clusters", "--db-cluster-identifier", cluster_id],
                    capture_output=True, text=True
                )
                if result.returncode == 0:
                    cluster_data = json.loads(result.stdout)
                    cluster = cluster_data['DBClusters'][0]
                    status["resources"]["aurora_cluster"] = {
                        "id": cluster_id,
                        "status": cluster['Status'],
                        "engine": cluster['Engine'],
                        "engine_version": cluster['EngineVersion'],
                        "endpoint": cluster.get('Endpoint', ''),
                        "reader_endpoint": cluster.get('ReaderEndpoint', ''),
                        "healthy": cluster['Status'] == 'available'
                    }
                    
                    # Check cluster instances
                    instances = []
                    for instance in cluster.get('DBClusterMembers', []):
                        instance_id = instance['DBInstanceIdentifier']
                        instance_result = subprocess.run(
                            ["aws", "rds", "describe-db-instances", "--db-instance-identifier", instance_id],
                            capture_output=True, text=True
                        )
                        if instance_result.returncode == 0:
                            instance_data = json.loads(instance_result.stdout)
                            db_instance = instance_data['DBInstances'][0]
                            instances.append({
                                "id": instance_id,
                                "status": db_instance['DBInstanceStatus'],
                                "instance_class": db_instance['DBInstanceClass'],
                                "availability_zone": db_instance['AvailabilityZone'],
                                "healthy": db_instance['DBInstanceStatus'] == 'available'
                            })
                    
                    status["resources"]["aurora_cluster"]["instances"] = instances
            
            # Check DynamoDB table status
            if 'dynamodb_table_name' in outputs:
                table_name = outputs['dynamodb_table_name']['value']
                result = subprocess.run(
                    ["aws", "dynamodb", "describe-table", "--table-name", table_name],
                    capture_output=True, text=True
                )
                if result.returncode == 0:
                    table_data = json.loads(result.stdout)
                    table = table_data['Table']
                    status["resources"]["dynamodb_table"] = {
                        "name": table_name,
                        "status": table['TableStatus'],
                        "billing_mode": table['BillingModeSummary']['BillingMode'],
                        "item_count": table.get('ItemCount', 0),
                        "size_bytes": table.get('TableSizeBytes', 0),
                        "healthy": table['TableStatus'] == 'ACTIVE'
                    }
            
            # Check security groups
            if 'database_security_group_id' in outputs:
                sg_id = outputs['database_security_group_id']['value']
                result = subprocess.run(
                    ["aws", "ec2", "describe-security-groups", "--group-ids", sg_id],
                    capture_output=True, text=True
                )
                if result.returncode == 0:
                    sg_data = json.loads(result.stdout)
                    sg = sg_data['SecurityGroups'][0]
                    status["resources"]["database_security_group"] = {
                        "id": sg_id,
                        "name": sg['GroupName'],
                        "description": sg['Description'],
                        "vpc_id": sg['VpcId'],
                        "rules_count": len(sg['IpPermissions']) + len(sg['IpPermissionsEgress'])
                    }
            
        except Exception as e:
            logger.error(f"Error getting resource status: {e}")
        
        return status
    
    def check_connectivity(self) -> Dict:
        """Check connectivity to key resources."""
        logger.info("Checking connectivity...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            return {}
        
        connectivity = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "connectivity": {}
        }
        
        try:
            # Check Aurora connectivity (if endpoint is available)
            if 'aurora_cluster_endpoint' in outputs:
                endpoint = outputs['aurora_cluster_endpoint']['value']
                if endpoint:
                    # Try to connect using psql (if available)
                    try:
                        result = subprocess.run(
                            ["psql", "-h", endpoint, "-U", "mtk_admin", "-d", "mtk_backend", "-c", "SELECT 1"],
                            capture_output=True, text=True, timeout=10
                        )
                        connectivity["connectivity"]["aurora"] = {
                            "endpoint": endpoint,
                            "reachable": result.returncode == 0,
                            "response_time": "N/A"  # Could be enhanced with timing
                        }
                    except (FileNotFoundError, subprocess.TimeoutExpired):
                        connectivity["connectivity"]["aurora"] = {
                            "endpoint": endpoint,
                            "reachable": "psql not available or timeout",
                            "response_time": "N/A"
                        }
            
            # Check DynamoDB connectivity
            if 'dynamodb_table_name' in outputs:
                table_name = outputs['dynamodb_table_name']['value']
                try:
                    result = subprocess.run(
                        ["aws", "dynamodb", "describe-table", "--table-name", table_name],
                        capture_output=True, text=True, timeout=10
                    )
                    connectivity["connectivity"]["dynamodb"] = {
                        "table": table_name,
                        "reachable": result.returncode == 0,
                        "response_time": "N/A"
                    }
                except subprocess.TimeoutExpired:
                    connectivity["connectivity"]["dynamodb"] = {
                        "table": table_name,
                        "reachable": False,
                        "response_time": "timeout"
                    }
        
        except Exception as e:
            logger.error(f"Error checking connectivity: {e}")
        
        return connectivity
    
    def get_cloudwatch_metrics(self) -> Dict:
        """Get CloudWatch metrics for deployed resources."""
        logger.info("Getting CloudWatch metrics...")
        
        outputs = self.tf_manager.get_outputs()
        if not outputs:
            return {}
        
        metrics = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "metrics": {}
        }
        
        try:
            # Get Aurora metrics
            if 'aurora_cluster_id' in outputs:
                cluster_id = outputs['aurora_cluster_id']['value']
                
                # CPU utilization
                result = subprocess.run([
                    "aws", "cloudwatch", "get-metric-statistics",
                    "--namespace", "AWS/RDS",
                    "--metric-name", "CPUUtilization",
                    "--dimensions", f"Name=DBClusterIdentifier,Value={cluster_id}",
                    "--start-time", time.strftime("%Y-%m-%dT%H:%M:%S", time.localtime(time.time() - 3600)),
                    "--end-time", time.strftime("%Y-%m-%dT%H:%M:%S"),
                    "--period", "300",
                    "--statistics", "Average"
                ], capture_output=True, text=True)
                
                if result.returncode == 0:
                    cpu_data = json.loads(result.stdout)
                    metrics["metrics"]["aurora_cpu"] = {
                        "datapoints": cpu_data.get('Datapoints', [])
                    }
                
                # Database connections
                result = subprocess.run([
                    "aws", "cloudwatch", "get-metric-statistics",
                    "--namespace", "AWS/RDS",
                    "--metric-name", "DatabaseConnections",
                    "--dimensions", f"Name=DBClusterIdentifier,Value={cluster_id}",
                    "--start-time", time.strftime("%Y-%m-%dT%H:%M:%S", time.localtime(time.time() - 3600)),
                    "--end-time", time.strftime("%Y-%m-%dT%H:%M:%S"),
                    "--period", "300",
                    "--statistics", "Average"
                ], capture_output=True, text=True)
                
                if result.returncode == 0:
                    conn_data = json.loads(result.stdout)
                    metrics["metrics"]["aurora_connections"] = {
                        "datapoints": conn_data.get('Datapoints', [])
                    }
            
            # Get DynamoDB metrics
            if 'dynamodb_table_name' in outputs:
                table_name = outputs['dynamodb_table_name']['value']
                
                # Consumed read capacity
                result = subprocess.run([
                    "aws", "cloudwatch", "get-metric-statistics",
                    "--namespace", "AWS/DynamoDB",
                    "--metric-name", "ConsumedReadCapacityUnits",
                    "--dimensions", f"Name=TableName,Value={table_name}",
                    "--start-time", time.strftime("%Y-%m-%dT%H:%M:%S", time.localtime(time.time() - 3600)),
                    "--end-time", time.strftime("%Y-%m-%dT%H:%M:%S"),
                    "--period", "300",
                    "--statistics", "Sum"
                ], capture_output=True, text=True)
                
                if result.returncode == 0:
                    read_data = json.loads(result.stdout)
                    metrics["metrics"]["dynamodb_read_capacity"] = {
                        "datapoints": read_data.get('Datapoints', [])
                    }
                
                # Consumed write capacity
                result = subprocess.run([
                    "aws", "cloudwatch", "get-metric-statistics",
                    "--namespace", "AWS/DynamoDB",
                    "--metric-name", "ConsumedWriteCapacityUnits",
                    "--dimensions", f"Name=TableName,Value={table_name}",
                    "--start-time", time.strftime("%Y-%m-%dT%H:%M:%S", time.localtime(time.time() - 3600)),
                    "--end-time", time.strftime("%Y-%m-%dT%H:%M:%S"),
                    "--period", "300",
                    "--statistics", "Sum"
                ], capture_output=True, text=True)
                
                if result.returncode == 0:
                    write_data = json.loads(result.stdout)
                    metrics["metrics"]["dynamodb_write_capacity"] = {
                        "datapoints": write_data.get('Datapoints', [])
                    }
        
        except Exception as e:
            logger.error(f"Error getting CloudWatch metrics: {e}")
        
        return metrics
    
    def generate_health_report(self) -> Dict:
        """Generate a comprehensive health report."""
        logger.info("Generating health report...")
        
        report = {
            "environment": self.environment,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "overall_health": "unknown",
            "summary": {}
        }
        
        # Get resource status
        status = self.get_resource_status()
        if status:
            report["resource_status"] = status
        
        # Get connectivity status
        connectivity = self.check_connectivity()
        if connectivity:
            report["connectivity"] = connectivity
        
        # Get metrics
        metrics = self.get_cloudwatch_metrics()
        if metrics:
            report["metrics"] = metrics
        
        # Determine overall health
        healthy_resources = 0
        total_resources = 0
        
        if "resource_status" in report and "resources" in report["resource_status"]:
            for resource_type, resource_data in report["resource_status"]["resources"].items():
                if isinstance(resource_data, dict) and "healthy" in resource_data:
                    total_resources += 1
                    if resource_data["healthy"]:
                        healthy_resources += 1
        
        if total_resources > 0:
            health_percentage = (healthy_resources / total_resources) * 100
            if health_percentage >= 90:
                report["overall_health"] = "healthy"
            elif health_percentage >= 70:
                report["overall_health"] = "warning"
            else:
                report["overall_health"] = "critical"
            
            report["summary"]["healthy_resources"] = healthy_resources
            report["summary"]["total_resources"] = total_resources
            report["summary"]["health_percentage"] = health_percentage
        
        return report

def main():
    parser = argparse.ArgumentParser(
        description="MTK Backend Terraform Monitoring Script",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python terraform_monitor.py dev status
  python terraform_monitor.py qa connectivity
  python terraform_monitor.py prod metrics
  python terraform_monitor.py dev health-report
        """
    )
    
    parser.add_argument(
        "environment",
        choices=["dev", "qa", "prod"],
        help="Environment to monitor"
    )
    
    parser.add_argument(
        "action",
        choices=["status", "connectivity", "metrics", "health-report"],
        help="Monitoring action to perform"
    )
    
    parser.add_argument(
        "--terraform-dir",
        default="iac",
        help="Directory containing Terraform files (default: iac)"
    )
    
    parser.add_argument(
        "--output-file",
        help="Output file for the report (JSON format)"
    )
    
    args = parser.parse_args()
    
    try:
        # Initialize monitor
        monitor = TerraformMonitor(args.environment, args.terraform_dir)
        
        # Perform the requested action
        result = None
        
        if args.action == "status":
            result = monitor.get_resource_status()
        
        elif args.action == "connectivity":
            result = monitor.check_connectivity()
        
        elif args.action == "metrics":
            result = monitor.get_cloudwatch_metrics()
        
        elif args.action == "health-report":
            result = monitor.generate_health_report()
        
        # Output the result
        if result:
            if args.output_file:
                with open(args.output_file, 'w') as f:
                    json.dump(result, f, indent=2)
                logger.info(f"Report saved to {args.output_file}")
            else:
                print(json.dumps(result, indent=2))
            
            sys.exit(0)
        else:
            logger.error("Failed to generate report")
            sys.exit(1)
    
    except Exception as e:
        logger.error(f"Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main() 