# MTK Backend Deployment Scripts

This directory contains comprehensive deployment scripts for the MTK Backend infrastructure, designed to work with the new naming convention: `ServiceType-Purpose-Region-AvailabilityZone`.

## Overview

The deployment scripts provide automated deployment, management, and monitoring capabilities for the MTK Backend infrastructure, which includes:

- **Network Infrastructure** (VPC, Subnets, Security Groups)
- **Database Layer** (Aurora PostgreSQL)
- **NoSQL Storage** (DynamoDB)
- **Serverless Functions** (Lambda)
- **API Gateway** (REST API with WAF and Cognito)
- **Monitoring** (CloudWatch)

## Scripts Overview

### 1. `terraform_deploy.py` - Main Deployment Script
The primary deployment script that handles complete infrastructure deployment.

**Features:**
- Complete deployment workflow
- Prerequisites checking
- Configuration validation
- Resource verification
- Deployment information retrieval

**Usage:**
```bash
# Deploy to dev environment
python terraform_deploy.py dev

# Deploy to qa with auto-approve
python terraform_deploy.py qa --auto-approve

# Deploy to prod with detailed output
python terraform_deploy.py prod --detailed

# Get deployment information
python terraform_deploy.py dev --info

# Destroy infrastructure
python terraform_deploy.py dev --destroy --auto-approve
```

### 2. `terraform_manager.py` - Terraform Operations Manager
Low-level Terraform operations manager for advanced users.

**Features:**
- Terraform state management
- Workspace operations
- Resource import capabilities
- Output and state inspection

**Usage:**
```bash
# Initialize Terraform
python terraform_manager.py dev init

# Validate configuration
python terraform_manager.py qa validate

# Create plan
python terraform_manager.py prod plan --detailed

# Apply changes
python terraform_manager.py dev apply --auto-approve

# Get outputs
python terraform_manager.py qa output

# Get state
python terraform_manager.py prod state

# Workspace operations
python terraform_manager.py dev workspace
python terraform_manager.py dev workspace --workspace feature-branch
```

### 3. `apigateway_deploy.py` - API Gateway Specialist
Specialized deployment script for API Gateway components.

**Features:**
- API Gateway deployment
- WAF configuration
- Cognito integration
- Endpoint testing
- Deployment verification

**Usage:**
```bash
# Deploy API Gateway
python apigateway_deploy.py dev

# Deploy with auto-approve
python apigateway_deploy.py qa --auto-approve

# Get API Gateway information
python apigateway_deploy.py prod --info

# Test API Gateway endpoint
python apigateway_deploy.py dev --test
```

### 4. `terraform_monitor.py` - Infrastructure Monitoring
Real-time monitoring and health checking for deployed infrastructure.

**Features:**
- Resource health monitoring
- Performance metrics
- Alerting capabilities
- Cost tracking

**Usage:**
```bash
# Monitor all resources
python terraform_monitor.py dev

# Monitor specific resource type
python terraform_monitor.py qa --resource-type lambda

# Continuous monitoring
python terraform_monitor.py prod --continuous --interval 300
```

### 5. `database_version_checker.py` - Database Management
Database version checking and migration management.

**Features:**
- Aurora version checking
- Migration planning
- Backup verification
- Performance analysis

**Usage:**
```bash
# Check database versions
python database_version_checker.py dev

# Plan migrations
python database_version_checker.py qa --plan-migration

# Verify backups
python database_version_checker.py prod --verify-backups
```

## Naming Convention Integration

All scripts are updated to work with the new naming convention: `ServiceType-Purpose-Region-AvailabilityZone`

### Resource Naming Examples:
- **VPC**: `VPC-MTKBackend-us-east-1-us-east-1a`
- **Aurora Cluster**: `Aurora-MTKBackend-us-east-1-us-east-1a`
- **DynamoDB Table**: `DynamoDB-UserLocations-MTKBackend-us-east-1-us-east-1a`
- **Lambda Functions**: `Lambda-CreateEntry-MTKBackend-us-east-1-us-east-1a`
- **API Gateway**: `APIGateway-MTKBackend-us-east-1-us-east-1a`

### Environment Variables:
The scripts automatically handle environment variables for the new naming convention:
- `DYNAMODB_TABLE_NAME`: Dynamically set based on region and AZ
- `AWS_REGION`: Used in resource naming
- `ENVIRONMENT`: Used for environment-specific configurations

## Prerequisites

### Required Software:
1. **Python 3.8+**
2. **Terraform 1.0+**
3. **AWS CLI 2.0+**
4. **Docker** (for Lambda builds)

### Required Python Packages:
```bash
pip install -r requirements.txt
```

### AWS Configuration:
```bash
# Configure AWS credentials
aws configure

# Set default region
export AWS_DEFAULT_REGION=us-east-1
```

### Terraform Configuration:
```bash
# Initialize Terraform
cd iac
terraform init
```

## Environment Configuration

### Environment Variables:
Each environment has its own configuration file:
- `iac/terraform.tfvars.dev`
- `iac/terraform.tfvars.qa`
- `iac/terraform.tfvars.prod`

### Required Variables:
```hcl
# Environment
environment = "dev"
aws_region = "us-east-1"
availability_zone = "us-east-1a"

# Database
db_password = "your-secure-password"
db_username = "mtk_admin"

# API Gateway
api_gateway_callback_urls = ["https://your-domain.com/callback"]
api_gateway_logout_urls = ["https://your-domain.com/logout"]

# Lambda
lambda_jar_path = "../../src/lambda/geo/cqrs/target/cqrs-lambda-1.0.0.jar"
```

## Deployment Workflow

### 1. Initial Setup
```bash
# Clone repository
git clone <repository-url>
cd mind_the_kid_backend

# Install dependencies
pip install -r scripts/requirements.txt

# Configure AWS credentials
aws configure
```

### 2. Build Lambda Functions
```bash
# Build Java Lambda functions
cd src/lambda/geo/cqrs
./build.sh
cd ../../../..
```

### 3. Deploy Infrastructure
```bash
# Deploy to dev environment
python scripts/terraform_deploy.py dev --auto-approve

# Deploy to qa environment
python scripts/terraform_deploy.py qa --auto-approve

# Deploy to prod environment
python scripts/terraform_deploy.py prod --auto-approve
```

### 4. Verify Deployment
```bash
# Get deployment information
python scripts/terraform_deploy.py dev --info

# Test API Gateway
python scripts/apigateway_deploy.py dev --test

# Monitor resources
python scripts/terraform_monitor.py dev
```

## Module-Specific Deployment

### Deploy Individual Modules:
```bash
# Deploy network only
python scripts/terraform_deploy.py dev --tf-file network --auto-approve

# Deploy database only
python scripts/terraform_deploy.py qa --tf-file database --auto-approve

# Deploy DynamoDB only
python scripts/terraform_deploy.py prod --tf-file dynamodb --auto-approve

# Deploy Lambda only
python scripts/terraform_deploy.py dev --tf-file lambda --auto-approve

# Deploy API Gateway only
python scripts/terraform_deploy.py qa --tf-file apigateway --auto-approve
```

## Monitoring and Maintenance

### Health Checks:
```bash
# Monitor all resources
python scripts/terraform_monitor.py dev --continuous

# Check specific resource health
python scripts/terraform_monitor.py qa --resource-type lambda

# Monitor costs
python scripts/terraform_monitor.py prod --cost-tracking
```

### Backup Verification:
```bash
# Verify database backups
python scripts/database_version_checker.py dev --verify-backups

# Check backup retention
python scripts/database_version_checker.py qa --backup-retention
```

## Troubleshooting

### Common Issues:

1. **Terraform State Lock:**
   ```bash
   # Force unlock state
   terraform force-unlock <lock-id>
   ```

2. **Resource Naming Conflicts:**
   ```bash
   # Check existing resources
   aws resourcegroupstaggingapi get-resources --tag-filters Key=Project,Values=MTKBackend
   ```

3. **Lambda Build Issues:**
   ```bash
   # Clean and rebuild
   cd src/lambda/geo/cqrs
   mvn clean package
   ```

4. **Permission Issues:**
   ```bash
   # Verify AWS permissions
   aws sts get-caller-identity
   aws iam get-user
   ```

### Log Files:
- `terraform_deploy.log`: Main deployment logs
- `terraform_manager.log`: Terraform operations logs
- `apigateway_deploy.log`: API Gateway deployment logs
- `terraform_monitor.log`: Monitoring logs

## Security Considerations

### IAM Permissions:
Ensure your AWS user/role has the following permissions:
- `AmazonEC2FullAccess`
- `AmazonRDSFullAccess`
- `AmazonDynamoDBFullAccess`
- `AWSLambda_FullAccess`
- `AmazonAPIGatewayAdministrator`
- `AmazonCognitoPowerUser`
- `AWSWAFFullAccess`
- `CloudWatchFullAccess`

### Secrets Management:
- Use AWS Secrets Manager for sensitive data
- Rotate database passwords regularly
- Use least privilege principle for IAM roles

## Cost Optimization

### Monitoring Costs:
```bash
# Track resource costs
python scripts/terraform_monitor.py dev --cost-tracking

# Identify cost optimization opportunities
python scripts/terraform_monitor.py qa --cost-analysis
```

### Resource Optimization:
- Use appropriate instance types
- Enable auto-scaling where applicable
- Monitor unused resources
- Implement proper tagging for cost allocation

## Best Practices

1. **Always test in dev first**
2. **Use version control for all changes**
3. **Monitor deployments continuously**
4. **Keep backups and disaster recovery plans**
5. **Document all customizations**
6. **Regular security audits**
7. **Cost monitoring and optimization**

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review log files
3. Verify AWS permissions
4. Check Terraform documentation
5. Contact the development team

## Changelog

### Version 2.0.0 (Current)
- Updated to new naming convention: `ServiceType-Purpose-Region-AvailabilityZone`
- Enhanced resource verification
- Improved error handling
- Added comprehensive monitoring
- Updated all scripts for consistency

### Version 1.0.0
- Initial release
- Basic deployment functionality
- Module-specific deployment
- Basic monitoring capabilities 