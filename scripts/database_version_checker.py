#!/usr/bin/env python3
"""
Database Version Checker and Migration Script

This script checks an Aurora PostgreSQL database for existence, verifies the version_info table,
and executes DDL scripts in version order to keep the database schema up to date.

Usage:
    python database_version_checker.py [--environment dev|qa|prod] [--region us-east-1]
"""

import os
import sys
import re
import glob
import logging
import argparse
import subprocess
from pathlib import Path
from typing import Optional, List, Tuple
from decimal import Decimal

import boto3
import psycopg2
from psycopg2.extras import RealDictCursor

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('database_migration.log')
    ]
)
logger = logging.getLogger(__name__)


class DatabaseVersionChecker:
    def __init__(self, environment: str, region: str):
        self.environment = environment
        self.region = region
        self.rds_client = boto3.client('rds', region_name=region)
        self.ec2_client = boto3.client('ec2', region_name=region)
        
        # Database connection parameters
        self.db_name = "mtk_backend"
        self.db_username = "mtk_admin"
        self.db_password = os.getenv('mtk_db_password')
        
        if not self.db_password:
            raise ValueError("Environment variable 'mtk_db_password' is required")
    
    def get_aurora_endpoint(self) -> Optional[str]:
        """Get the Aurora cluster endpoint from AWS."""
        try:
            cluster_identifier = f"{self.environment}-{self.region}-aurora-cluster"
            response = self.rds_client.describe_db_clusters(
                DBClusterIdentifier=cluster_identifier
            )
            
            if response['DBClusters']:
                cluster = response['DBClusters'][0]
                endpoint = cluster.get('Endpoint')
                if endpoint:
                    logger.info(f"Found Aurora cluster endpoint: {endpoint}")
                    return endpoint
                else:
                    logger.error("Aurora cluster found but no endpoint available")
                    return None
            else:
                logger.error(f"Aurora cluster '{cluster_identifier}' not found")
                return None
                
        except Exception as e:
            logger.error(f"Error getting Aurora endpoint: {e}")
            return None
    
    def test_database_connection(self, host: str, port: int = 5432) -> bool:
        """Test if we can connect to the database."""
        try:
            conn = psycopg2.connect(
                host=host,
                port=port,
                database=self.db_name,
                user=self.db_username,
                password=self.db_password,
                connect_timeout=10
            )
            conn.close()
            logger.info("Database connection successful")
            return True
        except Exception as e:
            logger.error(f"Database connection failed: {e}")
            return False
    
    def check_version_info_table(self, host: str, port: int = 5432) -> Optional[Decimal]:
        """Check if version_info table exists and get database_version."""
        try:
            conn = psycopg2.connect(
                host=host,
                port=port,
                database=self.db_name,
                user=self.db_username,
                password=self.db_password
            )
            
            with conn.cursor(cursor_factory=RealDictCursor) as cursor:
                # Check if version_info table exists
                cursor.execute("""
                    SELECT EXISTS (
                        SELECT FROM information_schema.tables 
                        WHERE table_schema = 'public' 
                        AND table_name = 'version_info'
                    );
                """)
                
                table_exists = cursor.fetchone()['exists']
                
                if not table_exists:
                    logger.info("version_info table does not exist")
                    return None
                
                # Check if database_version component exists
                cursor.execute("""
                    SELECT version_number 
                    FROM version_info 
                    WHERE component = 'database_version'
                """)
                
                result = cursor.fetchone()
                if result:
                    version = result['version_number']
                    logger.info(f"Current database version: {version}")
                    return version
                else:
                    logger.info("database_version component not found in version_info table")
                    return None
                    
        except Exception as e:
            logger.error(f"Error checking version_info table: {e}")
            return None
        finally:
            if 'conn' in locals():
                conn.close()
    
    def get_ddl_scripts(self) -> List[Tuple[str, Decimal]]:
        """Get all DDL scripts sorted by version number."""
        scripts_dir = Path(__file__).parent.parent / "data"
        pattern = "mtk_backend_ddl_v*.sql"
        
        scripts = []
        for script_path in scripts_dir.glob(pattern):
            # Extract version from filename
            match = re.search(r'mtk_backend_ddl_v(\d+\.\d+)\.sql', script_path.name)
            if match:
                version = Decimal(match.group(1))
                scripts.append((str(script_path), version))
        
        # Sort by version number
        scripts.sort(key=lambda x: x[1])
        logger.info(f"Found {len(scripts)} DDL scripts: {[f'v{v}' for _, v in scripts]}")
        return scripts
    
    def extract_version_from_script(self, script_path: str) -> Optional[Decimal]:
        """Extract version number from the first line of the DDL script."""
        try:
            with open(script_path, 'r') as f:
                first_line = f.readline().strip()
                match = re.search(r'#database_version=(\d+\.\d+)', first_line)
                if match:
                    return Decimal(match.group(1))
        except Exception as e:
            logger.error(f"Error reading script {script_path}: {e}")
        return None
    
    def execute_ddl_script(self, script_path: str, host: str, port: int = 5432) -> bool:
        """Execute a DDL script against the database."""
        try:
            # Read the script content
            with open(script_path, 'r') as f:
                script_content = f.read()
            
            # Connect to database
            conn = psycopg2.connect(
                host=host,
                port=port,
                database=self.db_name,
                user=self.db_username,
                password=self.db_password
            )
            conn.autocommit = False
            
            with conn.cursor() as cursor:
                # Execute the script
                logger.info(f"Executing script: {script_path}")
                cursor.execute(script_content)
                conn.commit()
                logger.info(f"Successfully executed script: {script_path}")
                return True
                
        except Exception as e:
            logger.error(f"Error executing script {script_path}: {e}")
            if 'conn' in locals():
                conn.rollback()
            return False
        finally:
            if 'conn' in locals():
                conn.close()
    
    def update_database_version(self, version: Decimal, host: str, port: int = 5432) -> bool:
        """Update the database_version in the version_info table."""
        try:
            conn = psycopg2.connect(
                host=host,
                port=port,
                database=self.db_name,
                user=self.db_username,
                password=self.db_password
            )
            
            with conn.cursor() as cursor:
                cursor.execute("""
                    INSERT INTO version_info (component, version_number) 
                    VALUES ('database_version', %s)
                    ON CONFLICT (component) 
                    DO UPDATE SET version_number = EXCLUDED.version_number
                """, (version,))
                
                conn.commit()
                logger.info(f"Updated database version to {version}")
                return True
                
        except Exception as e:
            logger.error(f"Error updating database version: {e}")
            if 'conn' in locals():
                conn.rollback()
            return False
        finally:
            if 'conn' in locals():
                conn.close()
    
    def run_migration(self):
        """Main migration process."""
        logger.info(f"Starting database migration for environment: {self.environment}")
        
        # Get Aurora endpoint
        endpoint = self.get_aurora_endpoint()
        if not endpoint:
            logger.error("Could not get Aurora endpoint")
            return False
        
        # Test database connection
        if not self.test_database_connection(endpoint):
            logger.error("Could not connect to database")
            return False
        
        # Check current database version
        current_version = self.check_version_info_table(endpoint)
        
        # Get all DDL scripts
        scripts = self.get_ddl_scripts()
        if not scripts:
            logger.error("No DDL scripts found")
            return False
        
        # Determine starting version
        if current_version is None:
            # Start from version 1.0 if no version info exists
            start_version = Decimal('1.0')
            logger.info(f"No version info found, starting from version {start_version}")
        else:
            start_version = current_version
            logger.info(f"Current database version: {current_version}")
        
        # Execute scripts in order
        for script_path, script_version in scripts:
            if script_version > start_version:
                logger.info(f"Executing script version {script_version}")
                
                # Verify script version matches filename
                script_file_version = self.extract_version_from_script(script_path)
                if script_file_version and script_file_version != script_version:
                    logger.warning(f"Version mismatch in {script_path}: filename={script_version}, content={script_file_version}")
                
                # Execute the script
                if self.execute_ddl_script(script_path, endpoint):
                    # Update database version
                    if self.update_database_version(script_version, endpoint):
                        logger.info(f"Successfully migrated to version {script_version}")
                    else:
                        logger.error(f"Failed to update database version to {script_version}")
                        return False
                else:
                    logger.error(f"Failed to execute script {script_path}")
                    return False
            else:
                logger.info(f"Skipping script version {script_version} (already applied)")
        
        logger.info("Database migration completed successfully")
        return True


def main():
    parser = argparse.ArgumentParser(description='Database Version Checker and Migration Script')
    parser.add_argument('--environment', choices=['dev', 'qa', 'prod'], default='dev',
                       help='Environment name (default: dev)')
    parser.add_argument('--region', default='us-east-1',
                       help='AWS region (default: us-east-1)')
    
    args = parser.parse_args()
    
    try:
        checker = DatabaseVersionChecker(args.environment, args.region)
        success = checker.run_migration()
        
        if success:
            logger.info("Migration completed successfully")
            sys.exit(0)
        else:
            logger.error("Migration failed")
            sys.exit(1)
            
    except Exception as e:
        logger.error(f"Unexpected error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main() 