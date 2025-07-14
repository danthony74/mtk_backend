# Naming Convention Update Summary

This document summarizes all the updates made to support the new naming convention: `ServiceType-Purpose-Region-AvailabilityZone`

## Overview

All AWS resources deployed via Terraform now follow a standardized naming pattern that provides clear identification of service type, purpose, region, and availability zone. This improves resource management, troubleshooting, and cost allocation.

## Updated Components

### 1. Terraform Infrastructure (`iac/`)

#### Network Module (`iac/modules/network/`)
**Updated Resources:**
- **VPC**: `VPC-MTKBackend-${region}-${az}`
- **Internet Gateway**: `IGW-MTKBackend-${region}-${az}`
- **Subnets**: `Subnet-Public/Private/Database-MTKBackend-${region}-${az}`
- **Route Tables**: `RouteTable-Public/Private-MTKBackend-${region}-${az}`
- **Security Groups**: `SecurityGroup-Database/Application-MTKBackend-${region}-${az}`
- **IPAM Resources**: `IPAM/IPAMPool-MTKBackend-${region}-${az}`

#### Database Module (`iac/modules/database/`)
**Updated Resources:**
- **Aurora Cluster**: `Aurora-MTKBackend-${region}-${az}`
- **Aurora Instances**: `AuroraInstance-MTKBackend-${region}-${az}-{instance_number}`
- **Subnet Group**: `SubnetGroup-Aurora-MTKBackend-${region}-${az}`
- **Parameter Groups**: `ParameterGroup/ClusterParameterGroup-Aurora-MTKBackend-${region}-${az}`
- **IAM Role**: `IAMRole-RDSMonitoring-MTKBackend-${region}-${az}`

#### DynamoDB Module (`iac/modules/dynamodb/`)
**Updated Resources:**
- **Table**: `DynamoDB-UserLocations-MTKBackend-${region}-${az}`
- **Stream Table**: `DynamoDB-UserLocationsStream-MTKBackend-${region}-${az}`
- **Auto Scaling Policies**: `AutoScalingPolicy-ReadCapacity/WriteCapacity-MTKBackend-${region}-${az}`

#### Lambda Module (`iac/modules/lambda/`)
**Updated Resources:**
- **Functions**: `Lambda-CreateEntry/BulkCreateEntry/RetrieveEntry/BulkRetrieveEntry/DeleteEntry/BulkDeleteEntry/CognitoAuthorizer-MTKBackend-${region}-${az}`
- **IAM Role**: `IAMRole-Lambda-MTKBackend-${region}-${az}`
- **IAM Policy**: `IAMPolicy-Lambda-MTKBackend-${region}-${az}`
- **Log Groups**: `LogGroup-{FunctionName}-MTKBackend-${region}-${az}`

#### API Gateway Module (`iac/modules/apigateway/`)
**Updated Resources:**
- **REST API**: `APIGateway-MTKBackend-${region}-${az}`
- **Stage**: `APIGatewayStage-MTKBackend-${region}-${az}`
- **Authorizer**: `Authorizer-Custom-MTKBackend-${region}-${az}`
- **Cognito Resources**: `CognitoUserPool/CognitoClient/CognitoDomain-MTKBackend-${region}-${az}`
- **WAF**: `WAF-APIGateway-MTKBackend-${region}-${az}`
- **CloudWatch Dashboard**: `Dashboard-APIGateway-MTKBackend-${region}-${az}`
- **IAM Roles/Policies**: `IAMRole/IAMPolicy-APIGateway{Type}-MTKBackend-${region}-${az}`

#### Main Configuration (`iac/main.tf`)
**Updated:**
- Added `availability_zone` variable to all module calls
- Updated default tags to include `ServiceType: "Backend"` and `Purpose: "MTKBackend"`
- Updated project name from "mtk_backend" to "MTKBackend"

#### Module Variables
**Updated:**
- Added `availability_zone` variable to all module variable files:
  - `iac/modules/network/variables.tf`
  - `iac/modules/database/variables.tf`
  - `iac/modules/dynamodb/variables.tf`
  - `iac/modules/lambda/variables.tf`
  - `iac/modules/apigateway/variables.tf`

### 2. Java Lambda Code (`src/lambda/geo/cqrs/`)

#### DynamoDBService (`src/lambda/geo/cqrs/shared/services/DynamoDBService.java`)
**Updated:**
- Added environment variable support for table name
- Added `DYNAMODB_TABLE_NAME` environment variable requirement
- Added `getTableName()` method for debugging
- Enhanced error handling for missing environment variables

#### UserLocation Model (`src/lambda/geo/cqrs/shared/models/UserLocation.java`)
**Updated:**
- Maintained compatibility with DynamoDB annotations
- Table name now handled dynamically through environment variables

### 3. Deployment Scripts (`scripts/`)

#### terraform_deploy.py
**Updated:**
- Enhanced resource verification to handle new naming convention
- Updated Lambda function verification to check multiple functions
- Improved error handling and logging
- Updated deployment information retrieval
- Simplified command-line interface

#### terraform_manager.py
**Updated:**
- Added support for new module names (`lambda` instead of `backend`)
- Enhanced command execution with better error handling
- Updated workspace operations
- Improved resource import capabilities
- Updated output and state inspection

#### apigateway_deploy.py
**Updated:**
- Enhanced API Gateway verification for new naming convention
- Updated Lambda function verification
- Improved deployment information retrieval
- Enhanced endpoint testing capabilities
- Updated error handling and logging

#### DEPLOYMENT_SCRIPTS_README.md
**Updated:**
- Complete rewrite to reflect new naming convention
- Updated usage examples with new command syntax
- Added comprehensive deployment workflow
- Enhanced troubleshooting section
- Added security and cost optimization sections

### 4. Documentation

#### TERRAFORM_NAMING_CONVENTION_README.md (New)
**Created:**
- Comprehensive documentation of the new naming convention
- Examples for all resource types
- Benefits and implementation details
- Usage examples and maintenance guidelines
- Migration notes from old naming pattern

## Environment Variables

### Required Environment Variables:
- `DYNAMODB_TABLE_NAME`: Dynamically set based on region and AZ
- `AWS_REGION`: Used in resource naming
- `ENVIRONMENT`: Used for environment-specific configurations
- `COGNITO_USER_POOL_ID`: For Lambda authorizer

### Lambda Environment Variables:
All Lambda functions now receive:
```bash
DYNAMODB_TABLE_NAME = "DynamoDB-UserLocations-MTKBackend-${region}-${az}"
ENVIRONMENT = "${environment}"
AWS_REGION = "${region}"
```

## Migration Impact

### Breaking Changes:
1. **Resource Names**: All AWS resources will have new names following the convention
2. **Environment Variables**: Lambda functions now require `DYNAMODB_TABLE_NAME`
3. **Deployment Scripts**: Updated command-line interfaces

### Backward Compatibility:
1. **Java Code**: Maintains compatibility with existing DynamoDB operations
2. **API Endpoints**: No changes to API Gateway endpoints
3. **Data**: No impact on existing data in DynamoDB

## Deployment Process

### New Deployment:
```bash
# Deploy with new naming convention
python scripts/terraform_deploy.py dev --auto-approve
```

### Migration from Old Naming:
1. **Backup existing state** (if applicable)
2. **Update variable files** with `availability_zone`
3. **Deploy with new naming convention**
4. **Verify all resources** are properly named
5. **Update monitoring and alerting** if needed

## Benefits of New Naming Convention

### 1. Clear Resource Identification
- Easy to identify service type and purpose
- Region and availability zone clearly visible
- Consistent naming across all modules

### 2. Improved Management
- Better resource organization in AWS console
- Easier cost allocation and tracking
- Simplified troubleshooting and debugging

### 3. Enhanced Scalability
- Supports multi-region deployments
- Clear regional resource identification
- Better support for disaster recovery

### 4. Compliance and Auditing
- Clear resource ownership
- Better audit trail
- Improved security posture

## Testing and Verification

### Resource Verification:
```bash
# Verify all resources with new names
python scripts/terraform_deploy.py dev --info

# Test API Gateway with new naming
python scripts/apigateway_deploy.py dev --test

# Monitor resources
python scripts/terraform_monitor.py dev
```

### AWS Console Verification:
1. **VPC**: Search for "VPC-MTKBackend"
2. **Aurora**: Search for "Aurora-MTKBackend"
3. **DynamoDB**: Search for "DynamoDB-UserLocations-MTKBackend"
4. **Lambda**: Search for "Lambda-*-MTKBackend"
5. **API Gateway**: Search for "APIGateway-MTKBackend"

## Future Considerations

### 1. Multi-Region Support
- Naming convention supports multi-region deployments
- Easy to identify resources across regions
- Consistent naming pattern for global resources

### 2. Cost Optimization
- Better cost allocation with clear resource identification
- Easier to identify unused resources
- Improved cost tracking and reporting

### 3. Monitoring and Alerting
- Update CloudWatch dashboards to use new resource names
- Update monitoring scripts and alerts
- Ensure all monitoring tools recognize new naming pattern

### 4. Documentation Updates
- Update runbooks and operational procedures
- Update monitoring and alerting documentation
- Update disaster recovery procedures

## Conclusion

The new naming convention provides significant improvements in resource management, troubleshooting, and operational efficiency. All components have been updated to support this convention while maintaining backward compatibility where possible.

The deployment scripts and infrastructure code are now ready for production use with the new naming convention, providing a more organized and maintainable infrastructure. 