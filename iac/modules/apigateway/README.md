# API Gateway Module

This Terraform module creates a comprehensive API Gateway setup with WAF protection, OAuth authentication via Cognito, and comprehensive monitoring.

## Features

### 🔐 Authentication & Authorization
- **Cognito User Pool**: Complete user management with email-based authentication
- **OAuth 2.0 Support**: Authorization code and implicit flows
- **API Gateway Authorizer**: Automatic token validation for protected endpoints
- **Custom Lambda Triggers**: Optional pre-authentication, pre-token generation, and user migration hooks

### 🛡️ Security
- **WAF v2 Protection**: Advanced web application firewall with multiple rule sets
- **Rate Limiting**: Configurable request rate limiting per IP
- **AWS Managed Rules**: Protection against common attacks (SQL injection, XSS, etc.)
- **Geo Restriction**: Optional country-based access control
- **CORS Support**: Built-in CORS configuration for web applications

### 📊 Monitoring & Logging
- **CloudWatch Logging**: Comprehensive API Gateway access logs
- **CloudWatch Dashboard**: Pre-configured monitoring dashboard
- **WAF Metrics**: Real-time WAF performance and security metrics
- **Custom Log Format**: Detailed request/response logging

### 🔧 Configuration
- **Regional Deployment**: Optimized for regional API Gateway deployment
- **Environment-based**: Separate configurations for dev, qa, and prod
- **Modular Design**: Easy to integrate with existing infrastructure

## Usage

### Basic Usage

```hcl
module "apigateway" {
  source = "./modules/apigateway"
  
  environment = "dev"
  aws_region  = "us-east-1"
  
  callback_urls = ["https://myapp.com/callback"]
  logout_urls   = ["https://myapp.com/logout"]
}
```

### Advanced Usage

```hcl
module "apigateway" {
  source = "./modules/apigateway"
  
  environment = "prod"
  aws_region  = "us-west-2"
  
  # OAuth Configuration
  callback_urls = [
    "https://myapp.com/callback",
    "https://staging.myapp.com/callback"
  ]
  logout_urls = [
    "https://myapp.com/logout",
    "https://staging.myapp.com/logout"
  ]
  
  # Lambda Triggers
  enable_lambda_triggers        = true
  pre_authentication_lambda_arn = "arn:aws:lambda:us-west-2:123456789012:function:pre-auth"
  pre_token_generation_lambda_arn = "arn:aws:lambda:us-west-2:123456789012:function:pre-token"
  
  # WAF Configuration
  waf_rate_limit = 5000
  geo_restriction_countries = ["CN", "RU", "KP"]
  enable_waf_geo_restriction = true
  
  # Logging
  api_gateway_log_retention_days = 30
  enable_cloudwatch_dashboard   = true
}
```

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| environment | Environment name (dev, qa, prod) | `string` | n/a | yes |
| aws_region | AWS region | `string` | n/a | yes |
| callback_urls | List of callback URLs for Cognito OAuth | `list(string)` | `["http://localhost:3000/callback"]` | no |
| logout_urls | List of logout URLs for Cognito OAuth | `list(string)` | `["http://localhost:3000/logout"]` | no |
| enable_lambda_triggers | Enable Lambda triggers for Cognito | `bool` | `false` | no |
| pre_authentication_lambda_arn | ARN of the pre-authentication Lambda function | `string` | `""` | no |
| pre_token_generation_lambda_arn | ARN of the pre-token generation Lambda function | `string` | `""` | no |
| user_migration_lambda_arn | ARN of the user migration Lambda function | `string` | `""` | no |
| geo_restriction_countries | List of country codes to block | `list(string)` | `[]` | no |
| waf_rate_limit | Rate limit for WAF (requests per 5 minutes) | `number` | `2000` | no |
| enable_waf_managed_rules | Enable AWS managed WAF rules | `bool` | `true` | no |
| enable_waf_rate_limiting | Enable WAF rate limiting | `bool` | `true` | no |
| enable_waf_geo_restriction | Enable WAF geo restriction | `bool` | `false` | no |
| api_gateway_log_retention_days | Number of days to retain API Gateway logs | `number` | `7` | no |
| enable_cloudwatch_dashboard | Enable CloudWatch dashboard for monitoring | `bool` | `true` | no |

## Outputs

| Name | Description |
|------|-------------|
| api_gateway_id | The ID of the API Gateway |
| api_gateway_invoke_url | The invoke URL of the API Gateway |
| api_gateway_stage_arn | The ARN of the API Gateway stage |
| cognito_user_pool_id | The ID of the Cognito User Pool |
| cognito_user_pool_client_id | The ID of the Cognito User Pool Client |
| cognito_user_pool_domain_url | The full URL of the Cognito User Pool domain |
| api_gateway_authorizer_id | The ID of the API Gateway authorizer |
| waf_web_acl_id | The ID of the WAF Web ACL |
| oauth_configuration | OAuth configuration for the API Gateway |
| api_endpoints | API Gateway endpoints |

## WAF Rules

The module includes the following WAF rules:

1. **Rate Limiting**: Limits requests per IP address (default: 2000 per 5 minutes)
2. **AWS Managed Rules - Common Rule Set**: Protection against common web exploits
3. **AWS Managed Rules - SQL Injection**: Protection against SQL injection attacks
4. **AWS Managed Rules - Known Bad Inputs**: Protection against XSS and other attacks
5. **Geo Restriction** (optional): Blocks requests from specified countries

## Cognito Configuration

### User Pool Features
- Email-based authentication
- Password policy with complexity requirements
- Optional MFA
- Email verification
- Account recovery via email
- Custom Lambda triggers support

### OAuth Scopes
- `phone`: Access to phone number
- `email`: Access to email address
- `openid`: OpenID Connect support
- `profile`: Access to profile information

### Token Validity
- Access Token: 1 hour
- ID Token: 1 hour
- Refresh Token: 30 days

## API Gateway Structure

```
/api/v1/
├── OPTIONS (CORS support)
└── [Your API endpoints]
```

## Security Considerations

1. **HTTPS Only**: All endpoints use HTTPS
2. **Token Validation**: All protected endpoints validate Cognito tokens
3. **WAF Protection**: Multiple layers of security rules
4. **Rate Limiting**: Prevents abuse and DDoS attacks
5. **Logging**: Comprehensive audit trail

## Monitoring

### CloudWatch Dashboard
The module creates a CloudWatch dashboard with:
- API Gateway request metrics
- Error rates (4xx, 5xx)
- WAF allowed/blocked requests
- Performance metrics

### Log Groups
- API Gateway access logs
- WAF logs (if enabled)
- Custom log format with detailed request information

## Deployment

### Using Terraform Manager

```bash
# Deploy all infrastructure
python scripts/terraform_deploy.py dev deploy --auto-approve

# Deploy only API Gateway
python scripts/terraform_deploy.py dev deploy --tf-file apigateway --auto-approve

# Deploy with specific configuration
python scripts/terraform_deploy.py prod deploy --tf-file apigateway --auto-approve
```

### Manual Terraform

```bash
cd iac
terraform init
terraform plan -var-file=terraform.tfvars.dev
terraform apply -var-file=terraform.tfvars.dev
```

## Integration Examples

### Frontend Integration

```javascript
// Cognito configuration
const cognitoConfig = {
  UserPoolId: 'us-east-1_xxxxxxxxx',
  ClientId: 'xxxxxxxxxxxxxxxxxxxxxxxxxx',
  Domain: 'dev-us-east-1-xxxxxxxx.auth.us-east-1.amazoncognito.com'
};

// API Gateway calls
const apiCall = async (endpoint) => {
  const token = await getCognitoToken();
  const response = await fetch(`${apiGatewayUrl}/api/v1/${endpoint}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.json();
};
```

### Backend Integration

```python
import boto3
import requests

# Verify Cognito token
def verify_token(token):
    client = boto3.client('cognito-idp')
    try:
        response = client.get_user(
            AccessToken=token
        )
        return response
    except Exception as e:
        return None

# Call API Gateway
def call_api_gateway(endpoint, token):
    headers = {
        'Authorization': f'Bearer {token}',
        'Content-Type': 'application/json'
    }
    response = requests.get(f'{api_gateway_url}/api/v1/{endpoint}', headers=headers)
    return response.json()
```

## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure callback URLs are properly configured
2. **Token Validation**: Check that tokens are not expired
3. **WAF Blocks**: Review WAF logs for blocked requests
4. **Rate Limiting**: Monitor rate limit metrics

### Debug Commands

```bash
# Check API Gateway logs
aws logs tail /aws/apigateway/dev-us-east-1-api --follow

# Check WAF metrics
aws cloudwatch get-metric-statistics \
  --namespace AWS/WAFV2 \
  --metric-name BlockedRequests \
  --dimensions Name=WebACL,Value=dev-us-east-1-api-gateway-waf \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-01T23:59:59Z \
  --period 300 \
  --statistics Sum

# Test API Gateway endpoint
curl -H "Authorization: Bearer YOUR_TOKEN" \
     https://your-api-gateway-url.amazonaws.com/dev/api/v1/health
``` 