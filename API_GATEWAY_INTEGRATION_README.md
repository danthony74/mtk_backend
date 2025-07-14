# API Gateway Integration for MTK Backend

This document describes the complete API Gateway setup for the Mind The Kid backend, including custom authorization, Lambda integrations, and deployment configuration.

## Overview

The API Gateway is configured with:
- **Custom Cognito Authorizer**: Validates JWT tokens and extracts user ID
- **CQRS Lambda Functions**: Handle all CRUD operations for user location data
- **WAF Protection**: Rate limiting and security rules
- **CloudWatch Logging**: Comprehensive monitoring and logging
- **CORS Support**: Cross-origin resource sharing enabled

## API Endpoints

### Base URL
```
https://{api-gateway-id}.execute-api.{region}.amazonaws.com/{environment}/api/v1
```

### Available Endpoints

#### Create Operations
- **POST** `/locations` - Create a single user location entry
- **POST** `/locations/bulk` - Create multiple user location entries

#### Retrieve Operations
- **GET** `/locations/{userId}` - Retrieve all locations for a user (with optional time range filters)
- **GET** `/locations/{userId}/{dateTime}` - Retrieve a specific location entry

#### Delete Operations
- **DELETE** `/locations/{userId}` - Delete all locations for a user (with optional time range filters)
- **DELETE** `/locations/{userId}/{dateTime}` - Delete a specific location entry

### Query Parameters for Time Range Filtering

For retrieve and delete operations on `/locations/{userId}`, you can use:
- `startTime` - ISO 8601 timestamp (e.g., "2024-01-01T10:00:00Z")
- `endTime` - ISO 8601 timestamp (e.g., "2024-01-01T12:00:00Z")

Examples:
- `GET /locations/user123?startTime=2024-01-01T10:00:00Z&endTime=2024-01-01T12:00:00Z`
- `GET /locations/user123?startTime=2024-01-01T10:00:00Z` (from start time onwards)
- `GET /locations/user123?endTime=2024-01-01T12:00:00Z` (up to end time)

## Authentication & Authorization

### Custom Cognito Authorizer

The API uses a custom Lambda authorizer that:
1. **Validates JWT tokens** from Cognito User Pool
2. **Extracts user ID** from the token claims
3. **Enforces access control** - users can only access their own data
4. **Returns authorization context** with user ID for downstream Lambda functions

### Authorization Flow

1. Client sends request with `Authorization: Bearer <jwt-token>` header
2. API Gateway invokes the custom authorizer Lambda
3. Authorizer validates the JWT token against Cognito
4. If valid, authorizer returns policy with user ID in context
5. API Gateway passes user ID to the target Lambda function
6. Lambda function validates that the requesting user matches the target user

### Error Responses

- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: User trying to access another user's data
- **400 Bad Request**: Invalid request parameters
- **404 Not Found**: Requested data not found
- **500 Internal Server Error**: Server-side errors

## Lambda Functions

### CQRS Architecture

The backend follows Command Query Responsibility Segregation (CQRS):

#### Commands (Write Operations)
- `CreateEntryHandler` - Single location creation
- `BulkCreateEntryHandler` - Multiple location creation
- `DeleteEntryHandler` - Single location deletion
- `BulkDeleteEntryHandler` - Multiple location deletion

#### Queries (Read Operations)
- `RetrieveEntryHandler` - Single location retrieval
- `BulkRetrieveEntryHandler` - Multiple location retrieval

#### Shared Components
- `DynamoDBService` - Data access layer
- `UserLocation` - Data model
- `ApiResponse` - Standardized response format

### Package Structure
```
com.mindthekid.geo.cqrs
├── commands/
│   ├── CreateEntryHandler.java
│   ├── BulkCreateEntryHandler.java
│   ├── DeleteEntryHandler.java
│   └── BulkDeleteEntryHandler.java
├── queries/
│   ├── RetrieveEntryHandler.java
│   └── BulkRetrieveEntryHandler.java
├── shared/
│   ├── models/
│   │   └── UserLocation.java
│   ├── dto/
│   │   └── ApiResponse.java
│   └── services/
│       └── DynamoDBService.java
└── infrastructure/
    └── CognitoAuthorizer.java
```

## Terraform Configuration

### Module Structure
```
iac/
├── main.tf                    # Main configuration
├── variables.tf              # Input variables
├── outputs.tf                # Output values
└── modules/
    ├── network/              # VPC and networking
    ├── database/             # Aurora database
    ├── dynamodb/             # DynamoDB table
    ├── lambda/               # Lambda functions
    └── apigateway/           # API Gateway and Cognito
```

### Key Resources Created

#### API Gateway
- REST API with regional endpoint
- Custom authorizer integration
- CORS configuration
- CloudWatch logging
- WAF Web ACL protection

#### Cognito
- User Pool with OAuth configuration
- User Pool Client
- Custom domain
- Lambda authorizer integration

#### Lambda Functions
- 6 CQRS Lambda functions
- 1 Custom authorizer Lambda
- IAM roles and policies
- CloudWatch log groups

#### Security
- WAF Web ACL with rate limiting
- AWS managed security rules
- IAM roles with least privilege
- VPC security groups

## Deployment

### Prerequisites
1. AWS CLI configured
2. Terraform installed (>= 1.0)
3. Java 11+ and Maven for Lambda builds
4. Appropriate AWS permissions

### Build Lambda Functions
```bash
cd src/lambda/geo/cqrs
mvn clean package
```

### Deploy Infrastructure
```bash
cd iac
terraform init
terraform plan -var-file=terraform.tfvars.dev
terraform apply -var-file=terraform.tfvars.dev
```

### Environment Variables
Configure the following in your `terraform.tfvars` files:
- `environment` - Environment name (dev, qa, prod)
- `aws_region` - AWS region
- `lambda_jar_path` - Path to compiled Lambda JAR
- `api_gateway_callback_urls` - OAuth callback URLs
- `api_gateway_logout_urls` - OAuth logout URLs

## Testing

### Unit Tests
All Lambda functions include comprehensive unit tests:
```bash
cd src/lambda/geo/cqrs
mvn test
```

### API Testing
Test the deployed API endpoints using tools like Postman or curl:

```bash
# Get user locations
curl -H "Authorization: Bearer <jwt-token>" \
     https://{api-gateway-id}.execute-api.{region}.amazonaws.com/{environment}/api/v1/locations/user123

# Create location entry
curl -X POST \
     -H "Authorization: Bearer <jwt-token>" \
     -H "Content-Type: application/json" \
     -d '{"latitude": 40.7128, "longitude": -74.0060, "timestamp": "2024-01-01T12:00:00Z"}' \
     https://{api-gateway-id}.execute-api.{region}.amazonaws.com/{environment}/api/v1/locations
```

## Monitoring

### CloudWatch Metrics
- API Gateway request count and latency
- Lambda function invocations and errors
- WAF blocked requests
- Custom authorizer performance

### CloudWatch Logs
- API Gateway access logs
- Lambda function logs
- Custom authorizer logs

### CloudWatch Dashboard
Automatically created dashboard with key metrics and visualizations.

## Security Features

1. **JWT Token Validation** - Secure token verification
2. **User Isolation** - Users can only access their own data
3. **Rate Limiting** - WAF-based request throttling
4. **Input Validation** - Comprehensive parameter validation
5. **Error Handling** - Secure error responses without data leakage
6. **CORS Configuration** - Controlled cross-origin access
7. **WAF Protection** - AWS managed security rules

## Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check JWT token validity
   - Verify Cognito User Pool configuration
   - Check authorizer Lambda logs

2. **403 Forbidden**
   - Ensure user ID in token matches target user ID
   - Check authorizer context extraction

3. **500 Internal Server Error**
   - Check Lambda function logs
   - Verify DynamoDB table permissions
   - Check environment variables

### Log Locations
- API Gateway: `/aws/apigateway/{api-name}`
- Lambda Functions: `/aws/lambda/{environment}-{region}-{function-name}`
- Custom Authorizer: `/aws/lambda/{environment}-{region}-cognito-authorizer`

## Future Enhancements

1. **API Versioning** - Support for multiple API versions
2. **Request/Response Transformation** - Custom request/response mapping
3. **Caching** - API Gateway response caching
4. **Usage Plans** - API usage throttling and quotas
5. **API Documentation** - OpenAPI/Swagger documentation
6. **Multi-region Deployment** - Global API distribution 