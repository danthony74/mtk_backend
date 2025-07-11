# MTK Backend Infrastructure as Code

This Terraform configuration creates a comprehensive AWS infrastructure for the MTK Backend application with Aurora PostgreSQL and DynamoDB, featuring multi-AZ and multi-region replication.

## Architecture Overview

### Components
- **VPC with IPAM**: Managed IP address space with IPAM
- **Aurora PostgreSQL**: Multi-AZ cluster for structured user data
- **DynamoDB**: Global tables for location data with multi-region replication
- **Security Groups**: Proper network segmentation
- **Auto Scaling**: DynamoDB capacity management

### Network Layout
- Public subnets for internet-facing resources
- Private subnets for application servers
- Database subnets for Aurora instances
- IPAM-managed CIDR blocks

## Prerequisites

1. **AWS CLI** configured with appropriate credentials
2. **Terraform** >= 1.0 installed
3. **AWS Provider** access to required services:
   - VPC and IPAM
   - RDS Aurora
   - DynamoDB
   - IAM
   - CloudWatch

## Environment Configuration

### Development (`terraform.tfvars.dev`)
- Single region deployment
- Smaller instance sizes
- Reduced backup retention
- No deletion protection

### QA (`terraform.tfvars.qa`)
- Multi-region DynamoDB global tables
- Enhanced security settings
- Medium backup retention

### Production (`terraform.tfvars.prod`)
- Multi-region deployment
- Larger instance sizes
- Maximum security and backup retention
- Full deletion protection

## Deployment Instructions

### 1. Initialize Terraform
```bash
terraform init
```

### 2. Plan Deployment
```bash
# For development
terraform plan -var-file="terraform.tfvars.dev"

# For QA
terraform plan -var-file="terraform.tfvars.qa"

# For production
terraform plan -var-file="terraform.tfvars.prod"
```

### 3. Apply Configuration
```bash
# For development
terraform apply -var-file="terraform.tfvars.dev"

# For QA
terraform apply -var-file="terraform.tfvars.qa"

# For production
terraform apply -var-file="terraform.tfvars.prod"
```

### 4. Destroy Infrastructure (if needed)
```bash
terraform destroy -var-file="terraform.tfvars.dev"
```

## DynamoDB Schema

The DynamoDB table `user_locations` contains:

| Attribute | Type | Description |
|-----------|------|-------------|
| user_id | String (Hash Key) | Alphanumeric user identifier |
| date_time | String (Range Key) | ISO 8601 timestamp |
| lat_long | String | Latitude,Longitude format |
| is_private | Boolean | Privacy flag |
| is_real | Boolean | Data authenticity flag |

### Global Secondary Indexes
- `lat_long_index`: Query by location
- `is_private_index`: Query by privacy setting
- `is_real_index`: Query by data authenticity

## Security Features

- **Encryption at Rest**: All data encrypted
- **Encryption in Transit**: SSL/TLS for database connections
- **Network Security**: Security groups with minimal access
- **IAM Roles**: Least privilege access
- **Backup Protection**: Point-in-time recovery enabled

## Monitoring and Logging

- **CloudWatch Logs**: Database query logging
- **Performance Insights**: Database performance monitoring
- **Enhanced Monitoring**: RDS metrics collection
- **DynamoDB Streams**: Change tracking for global tables

## Cost Optimization

- **Aurora Serverless v2**: Auto-scaling database capacity
- **DynamoDB On-Demand**: Pay-per-request billing
- **Auto Scaling**: Automatic capacity management
- **Reserved Instances**: Consider for production workloads

## Multi-Region Setup

For QA and Production environments:
1. Deploy primary region first
2. Deploy secondary region with same configuration
3. DynamoDB global tables will automatically sync
4. Aurora read replicas can be added in secondary region

## Troubleshooting

### Common Issues
1. **IPAM CIDR Conflicts**: Ensure unique CIDR blocks per environment
2. **Aurora Cluster Creation**: May take 10-15 minutes
3. **DynamoDB Global Tables**: Require streams enabled
4. **Security Group Rules**: Verify application access

### Useful Commands
```bash
# Check Aurora cluster status
aws rds describe-db-clusters --db-cluster-identifier <cluster-id>

# Monitor DynamoDB metrics
aws cloudwatch get-metric-statistics --namespace AWS/DynamoDB

# Test database connectivity
psql -h <endpoint> -U <username> -d <database>
```

## Tags and Naming Convention

All resources follow the naming convention:
`{environment}_{region}_{zone}_{type}`

Example: `prod_us-east-1_us-east-1a_aurora_cluster`

## Support

For issues or questions:
1. Check Terraform logs
2. Review AWS CloudWatch metrics
3. Consult AWS documentation
4. Contact the infrastructure team 