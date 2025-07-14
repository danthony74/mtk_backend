# CQRS Lambda Functions

This directory contains the CQRS (Command Query Responsibility Segregation) Lambda functions for DynamoDB operations in the Mind The Kid backend.

## Architecture

The CQRS pattern separates read and write operations:

### Commands (Write Operations)
- `CreateEntryHandler` - Single entry creation
- `BulkCreateEntryHandler` - Bulk entry creation
- `DeleteEntryHandler` - Single entry deletion
- `BulkDeleteEntryHandler` - Bulk entry deletion

### Queries (Read Operations)
- `RetrieveEntryHandler` - Single entry retrieval
- `BulkRetrieveEntryHandler` - Bulk entry retrieval with time range support

### Shared Components
- `UserLocation` - Data model with DynamoDB annotations
- `ApiResponse` - Standardized API response format
- `DynamoDBService` - Shared service for database operations

## API Endpoints

### Create Operations

#### Single Create
- **POST** `/api/v1/locations`
- **Authorization**: Cognito User Pool
- **Body**: UserLocation object
```json
{
  "userId": "user123",
  "dateTime": "2024-01-01T12:00:00Z",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "isPrivate": false,
  "isReal": true
}
```

#### Bulk Create
- **POST** `/api/v1/locations/bulk`
- **Authorization**: Cognito User Pool
- **Body**: Array of UserLocation objects
```json
[
  {
    "userId": "user123",
    "dateTime": "2024-01-01T12:00:00Z",
    "latitude": 40.7128,
    "longitude": -74.0060
  },
  {
    "userId": "user123",
    "dateTime": "2024-01-01T13:00:00Z",
    "latitude": 40.7130,
    "longitude": -74.0062
  }
]
```

### Retrieve Operations

#### Single Retrieve
- **GET** `/api/v1/locations/{userId}/{dateTime}`
- **Authorization**: Cognito User Pool
- **Path Parameters**:
  - `userId`: User identifier
  - `dateTime`: Timestamp in ISO format

#### Bulk Retrieve
- **GET** `/api/v1/locations/{userId}`
- **Authorization**: Cognito User Pool
- **Path Parameters**:
  - `userId`: User identifier
- **Query Parameters** (optional):
  - `startTime`: Start timestamp (ISO format)
  - `endTime`: End timestamp (ISO format)

**Examples:**
- All entries: `GET /api/v1/locations/user123`
- Time range: `GET /api/v1/locations/user123?startTime=2024-01-01T00:00:00Z&endTime=2024-01-02T00:00:00Z`
- From time: `GET /api/v1/locations/user123?startTime=2024-01-01T00:00:00Z`
- Up to time: `GET /api/v1/locations/user123?endTime=2024-01-02T00:00:00Z`

### Delete Operations

#### Single Delete
- **DELETE** `/api/v1/locations/{userId}/{dateTime}`
- **Authorization**: Cognito User Pool
- **Path Parameters**:
  - `userId`: User identifier
  - `dateTime`: Timestamp in ISO format

#### Bulk Delete
- **DELETE** `/api/v1/locations/{userId}`
- **Authorization**: Cognito User Pool
- **Path Parameters**:
  - `userId`: User identifier
- **Query Parameters** (optional):
  - `startTime`: Start timestamp (ISO format)
  - `endTime`: End timestamp (ISO format)

## Response Format

All endpoints return a standardized response format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## DynamoDB Schema

The `UserLocation` table uses the following schema:

- **Hash Key**: `user_id` (String)
- **Range Key**: `date_time` (String)
- **GSI**: `lat_long_index` (lat_long, date_time)
- **GSI**: `is_private_index` (is_private, date_time)
- **GSI**: `is_real_index` (is_real, date_time)

### Attributes
- `user_id`: User identifier
- `date_time`: Timestamp in ISO format
- `latitude`: Latitude coordinate
- `longitude`: Longitude coordinate
- `is_private`: Privacy flag
- `is_real`: Real data flag
- `lat_long`: Composite coordinate string (auto-generated)
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

## Building

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Build the project
./build.sh

# Or manually with Maven
mvn clean package -DskipTests
```

The build creates a JAR file at `target/cqrs-lambda-1.0.0.jar`.

## Deployment

### Terraform Deployment

The Lambda functions are deployed using Terraform. The deployment includes:

1. **Lambda Functions**: 6 functions (3 commands, 3 queries)
2. **IAM Roles**: Permissions for DynamoDB and CloudWatch
3. **API Gateway Integration**: REST API with Cognito authorization
4. **CloudWatch Logs**: Logging for all functions
5. **WAF Protection**: Web Application Firewall rules

### Deployment Commands

```bash
# Deploy all infrastructure
cd iac
terraform init
terraform plan -var-file=terraform.tfvars.dev
terraform apply -var-file=terraform.tfvars.dev

# Deploy only Lambda functions
terraform apply -var-file=terraform.tfvars.dev -target=module.lambda
```

### Environment Variables

The Lambda functions use the following environment variables:
- `DYNAMODB_TABLE_NAME`: DynamoDB table name
- `ENVIRONMENT`: Environment name (dev, qa, prod)
- `AWS_REGION`: AWS region

## Monitoring

### CloudWatch Logs
Each Lambda function has its own CloudWatch log group:
- `/aws/lambda/{environment}-{region}-create-entry`
- `/aws/lambda/{environment}-{region}-bulk-create-entry`
- `/aws/lambda/{environment}-{region}-retrieve-entry`
- `/aws/lambda/{environment}-{region}-bulk-retrieve-entry`
- `/aws/lambda/{environment}-{region}-delete-entry`
- `/aws/lambda/{environment}-{region}-bulk-delete-entry`

### Metrics
- Invocation count
- Duration
- Error rate
- Throttle count

## Security

### Authentication
- All endpoints require Cognito User Pool authentication
- JWT tokens are validated by API Gateway

### Authorization
- Users can only access their own data (user_id validation)
- DynamoDB IAM policies restrict access to specific table

### Data Protection
- All data is encrypted at rest
- HTTPS/TLS for all API communications
- WAF protection against common attacks

## Performance

### Lambda Configuration
- **Memory**: 512MB (single operations), 1024MB (bulk operations)
- **Timeout**: 30 seconds (single operations), 60 seconds (bulk operations)
- **Runtime**: Java 11

### DynamoDB Optimization
- Batch operations for bulk functions
- Efficient queries using GSI
- Connection pooling via AWS SDK

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
# Test with AWS SAM Local
sam local invoke CreateEntryHandler -e events/create-entry.json
```

### API Testing
```bash
# Test with curl (requires authentication token)
curl -X POST https://api.example.com/api/v1/locations \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId":"test","dateTime":"2024-01-01T12:00:00Z","latitude":40.7128,"longitude":-74.0060}'
```

## Troubleshooting

### Common Issues

1. **Timeout Errors**: Increase Lambda timeout for bulk operations
2. **Memory Errors**: Increase Lambda memory allocation
3. **Permission Errors**: Check IAM roles and policies
4. **DynamoDB Errors**: Verify table exists and permissions are correct

### Debugging

1. Check CloudWatch logs for detailed error messages
2. Verify environment variables are set correctly
3. Test DynamoDB connectivity
4. Validate API Gateway configuration

## Contributing

1. Follow the existing code structure
2. Add unit tests for new functionality
3. Update documentation for API changes
4. Test thoroughly before deployment 