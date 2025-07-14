# Terraform Naming Convention

This document outlines the standardized naming convention used for all AWS resources deployed via Terraform in the MTK Backend infrastructure.

## Naming Pattern

All resources follow the pattern: `ServiceType-Purpose-Region-AvailabilityZone`

### Components:
- **ServiceType**: The AWS service type (e.g., VPC, Aurora, DynamoDB, Lambda, APIGateway)
- **Purpose**: The specific purpose or function (e.g., MTKBackend, UserLocations, CreateEntry)
- **Region**: AWS region (e.g., us-east-1, eu-west-1)
- **AvailabilityZone**: Primary availability zone (e.g., us-east-1a, eu-west-1a)

## Resource Naming Examples

### Network Resources
- **VPC**: `VPC-MTKBackend-${region}-${az}`
- **Internet Gateway**: `IGW-MTKBackend-${region}-${az}`
- **Subnets**: 
  - `Subnet-Public-MTKBackend-${region}-${az}`
  - `Subnet-Private-MTKBackend-${region}-${az}`
  - `Subnet-Database-MTKBackend-${region}-${az}`
- **Route Tables**: 
  - `RouteTable-Public-MTKBackend-${region}-${az}`
  - `RouteTable-Private-MTKBackend-${region}-${az}`
- **Security Groups**: 
  - `SecurityGroup-Database-MTKBackend-${region}-${az}`
  - `SecurityGroup-Application-MTKBackend-${region}-${az}`
- **IPAM**: 
  - `IPAM-MTKBackend-${region}-${az}`
  - `IPAMPool-MTKBackend-${region}-${az}`

### Database Resources
- **Aurora Cluster**: `Aurora-MTKBackend-${region}-${az}`
- **Aurora Instances**: `AuroraInstance-MTKBackend-${region}-${az}-{instance_number}`
- **Subnet Group**: `SubnetGroup-Aurora-MTKBackend-${region}-${az}`
- **Parameter Groups**: 
  - `ParameterGroup-Aurora-MTKBackend-${region}-${az}`
  - `ClusterParameterGroup-Aurora-MTKBackend-${region}-${az}`
- **IAM Role**: `IAMRole-RDSMonitoring-MTKBackend-${region}-${az}`

### DynamoDB Resources
- **Table**: `DynamoDB-UserLocations-MTKBackend-${region}-${az}`
- **Stream Table**: `DynamoDB-UserLocationsStream-MTKBackend-${region}-${az}`
- **Auto Scaling Policies**: 
  - `AutoScalingPolicy-ReadCapacity-MTKBackend-${region}-${az}`
  - `AutoScalingPolicy-WriteCapacity-MTKBackend-${region}-${az}`

### Lambda Resources
- **Functions**: 
  - `Lambda-CreateEntry-MTKBackend-${region}-${az}`
  - `Lambda-BulkCreateEntry-MTKBackend-${region}-${az}`
  - `Lambda-RetrieveEntry-MTKBackend-${region}-${az}`
  - `Lambda-BulkRetrieveEntry-MTKBackend-${region}-${az}`
  - `Lambda-DeleteEntry-MTKBackend-${region}-${az}`
  - `Lambda-BulkDeleteEntry-MTKBackend-${region}-${az}`
  - `Lambda-CognitoAuthorizer-MTKBackend-${region}-${az}`
- **IAM Role**: `IAMRole-Lambda-MTKBackend-${region}-${az}`
- **IAM Policy**: `IAMPolicy-Lambda-MTKBackend-${region}-${az}`
- **Log Groups**: 
  - `LogGroup-CreateEntry-MTKBackend-${region}-${az}`
  - `LogGroup-BulkCreateEntry-MTKBackend-${region}-${az}`
  - `LogGroup-RetrieveEntry-MTKBackend-${region}-${az}`
  - `LogGroup-BulkRetrieveEntry-MTKBackend-${region}-${az}`
  - `LogGroup-DeleteEntry-MTKBackend-${region}-${az}`
  - `LogGroup-BulkDeleteEntry-MTKBackend-${region}-${az}`
  - `LogGroup-CognitoAuthorizer-MTKBackend-${region}-${az}`

### API Gateway Resources
- **REST API**: `APIGateway-MTKBackend-${region}-${az}`
- **Stage**: `APIGatewayStage-MTKBackend-${region}-${az}`
- **Authorizer**: `Authorizer-Custom-MTKBackend-${region}-${az}`
- **IAM Roles**: 
  - `IAMRole-APIGatewayAuthorizer-MTKBackend-${region}-${az}`
  - `IAMRole-APIGatewayCloudWatch-MTKBackend-${region}-${az}`
- **IAM Policies**: 
  - `IAMPolicy-APIGatewayAuthorizer-MTKBackend-${region}-${az}`
  - `IAMPolicy-APIGatewayCloudWatch-MTKBackend-${region}-${az}`
- **Log Group**: `LogGroup-APIGateway-MTKBackend-${region}-${az}`

### Cognito Resources
- **User Pool**: `CognitoUserPool-MTKBackend-${region}-${az}`
- **Client**: `CognitoClient-MTKBackend-${region}-${az}`
- **Domain**: `CognitoDomain-MTKBackend-${region}-${random_suffix}`

### WAF Resources
- **Web ACL**: `WAF-APIGateway-MTKBackend-${region}-${az}`

### CloudWatch Resources
- **Dashboard**: `Dashboard-APIGateway-MTKBackend-${region}-${az}`

## Benefits of This Naming Convention

1. **Clear Identification**: Easy to identify the service type and purpose of each resource
2. **Regional Awareness**: Region and availability zone are clearly visible
3. **Consistency**: Uniform naming across all modules and resources
4. **Scalability**: Supports multi-region and multi-AZ deployments
5. **Troubleshooting**: Easier to locate and debug resources in AWS console
6. **Cost Tracking**: Better organization for cost allocation and billing
7. **Compliance**: Clear resource ownership and purpose for audit purposes

## Implementation

The naming convention is implemented through:

1. **Terraform Variables**: Each module accepts `availability_zone` variable
2. **Resource Tags**: All resources include standardized tags
3. **Default Tags**: Provider-level default tags for consistent labeling
4. **Module Integration**: All modules pass the availability zone to their resources

## Default Tags

All resources include the following default tags:
- `Environment`: Environment name (dev, qa, prod)
- `Region`: AWS region
- `Zone`: Primary availability zone
- `Type`: "infrastructure"
- `Project`: "MTKBackend"
- `ManagedBy`: "terraform"
- `ServiceType`: "Backend"
- `Purpose`: "MTKBackend"

## Usage Examples

### Deploying to Different Regions
```bash
# Deploy to us-east-1
terraform apply -var="aws_region=us-east-1" -var="availability_zone=us-east-1a"

# Deploy to eu-west-1
terraform apply -var="aws_region=eu-west-1" -var="availability_zone=eu-west-1a"
```

### Resource Identification
Resources can be easily identified in AWS Console:
- Search for "MTKBackend" to find all project resources
- Filter by region using the region suffix
- Identify service type by the prefix (VPC, Aurora, Lambda, etc.)

## Maintenance

When adding new resources:
1. Follow the naming pattern: `ServiceType-Purpose-Region-AvailabilityZone`
2. Include the `availability_zone` variable in module variables
3. Pass the variable to the module in main.tf
4. Use the variable in resource names and tags
5. Update this documentation with new resource types

## Migration Notes

This naming convention replaces the previous pattern that used:
- `${var.environment}_${var.aws_region}_resource_name`

The new convention provides better clarity and consistency across all AWS resources. 