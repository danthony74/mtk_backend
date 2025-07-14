# Cognito User Pool
resource "aws_cognito_user_pool" "main" {
  name = "CognitoUserPool-MTKBackend-${var.aws_region}-${var.availability_zone}"
  
  # Password policy
  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }
  
  # MFA configuration
  mfa_configuration = "OPTIONAL"
  
  # User pool attributes
  username_attributes = ["email"]
  
  # Auto verified attributes
  auto_verified_attributes = ["email"]
  
  # Email configuration
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }
  
  # Account recovery
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }
  
  # Lambda triggers (optional)
  dynamic "lambda_config" {
    for_each = var.enable_lambda_triggers ? [1] : []
    content {
      pre_authentication  = var.pre_authentication_lambda_arn
      pre_token_generation = var.pre_token_generation_lambda_arn
      user_migration      = var.user_migration_lambda_arn
    }
  }
  
  tags = {
    Name = "CognitoUserPool-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# Cognito User Pool Client
resource "aws_cognito_user_pool_client" "main" {
  name         = "CognitoClient-MTKBackend-${var.aws_region}-${var.availability_zone}"
  user_pool_id = aws_cognito_user_pool.main.id
  
  # OAuth configuration
  generate_secret = false
  
  # Allowed OAuth flows
  allowed_oauth_flows                  = ["code", "implicit"]
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_scopes                 = ["phone", "email", "openid", "profile"]
  
  # Callback URLs
  callback_urls = var.callback_urls
  logout_urls   = var.logout_urls
  
  # Supported identity providers
  supported_identity_providers = ["COGNITO"]
  
  # Token validity
  access_token_validity  = 1
  id_token_validity      = 1
  refresh_token_validity = 30
  
  # Prevent user existence errors
  prevent_user_existence_errors = "ENABLED"
  
  # Explicit auth flows
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
}

# Cognito User Pool Domain
resource "aws_cognito_user_pool_domain" "main" {
  domain       = "CognitoDomain-MTKBackend-${var.aws_region}-${random_string.domain_suffix.result}"
  user_pool_id = aws_cognito_user_pool.main.id
}

# Random string for domain suffix
resource "random_string" "domain_suffix" {
  length  = 8
  special = false
  upper   = false
}

# API Gateway REST API
resource "aws_api_gateway_rest_api" "main" {
  name        = "APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  description = "MTK Backend API Gateway"
  
  endpoint_configuration {
    types = ["REGIONAL"]
  }
  
  tags = {
    Name = "APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# API Gateway Authorizer
resource "aws_api_gateway_authorizer" "custom" {
  name                   = "Authorizer-Custom-MTKBackend-${var.aws_region}-${var.availability_zone}"
  type                   = "TOKEN"
  rest_api_id           = aws_api_gateway_rest_api.main.id
  authorizer_uri        = var.cognito_authorizer_lambda_invoke_arn
  authorizer_credentials = aws_iam_role.api_gateway_authorizer.arn
  identity_source       = "method.request.header.Authorization"
  authorizer_result_ttl_in_seconds = 300
}

# IAM Role for API Gateway Authorizer
resource "aws_iam_role" "api_gateway_authorizer" {
  name = "IAMRole-APIGatewayAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "apigateway.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "IAMRole-APIGatewayAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# IAM Policy for API Gateway Authorizer
resource "aws_iam_role_policy" "api_gateway_authorizer" {
  name = "IAMPolicy-APIGatewayAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role = aws_iam_role.api_gateway_authorizer.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "lambda:InvokeFunction"
        ]
        Resource = [var.cognito_authorizer_lambda_invoke_arn]
      }
    ]
  })
}

# API Gateway Resource (root)
resource "aws_api_gateway_resource" "root" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_rest_api.main.root_resource_id
  path_part   = "api"
}

# API Gateway Resource (v1)
resource "aws_api_gateway_resource" "v1" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_resource.root.id
  path_part   = "v1"
}

# API Gateway Resource (locations)
resource "aws_api_gateway_resource" "locations" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_resource.v1.id
  path_part   = "locations"
}

# API Gateway Resource (userId)
resource "aws_api_gateway_resource" "user_id" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_resource.locations.id
  path_part   = "{userId}"
}

# API Gateway Resource (dateTime)
resource "aws_api_gateway_resource" "date_time" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_resource.user_id.id
  path_part   = "{dateTime}"
}

# API Gateway Method (OPTIONS for CORS)
resource "aws_api_gateway_method" "options" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.v1.id
  http_method   = "OPTIONS"
  authorization = "NONE"
}

# API Gateway Integration (OPTIONS)
resource "aws_api_gateway_integration" "options" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.v1.id
  http_method = aws_api_gateway_method.options.http_method
  
  type = "MOCK"
  
  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

# API Gateway Method Response (OPTIONS)
resource "aws_api_gateway_method_response" "options" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.v1.id
  http_method = aws_api_gateway_method.options.http_method
  status_code = "200"
  
  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }
}

# API Gateway Integration Response (OPTIONS)
resource "aws_api_gateway_integration_response" "options" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.v1.id
  http_method = aws_api_gateway_method.options.http_method
  status_code = aws_api_gateway_method_response.options.status_code
  
  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,POST,PUT,DELETE,OPTIONS'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }
}

# Lambda Integrations for CQRS Operations

# Create Entry - POST /api/v1/locations
resource "aws_api_gateway_method" "create_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.locations.id
  http_method   = "POST"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "create_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.locations.id
  http_method = aws_api_gateway_method.create_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.create_entry_lambda_invoke_arn
}

# Bulk Create Entry - POST /api/v1/locations/bulk
resource "aws_api_gateway_resource" "bulk" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_resource.locations.id
  path_part   = "bulk"
}

resource "aws_api_gateway_method" "bulk_create_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.bulk.id
  http_method   = "POST"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "bulk_create_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.bulk.id
  http_method = aws_api_gateway_method.bulk_create_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.bulk_create_entry_lambda_invoke_arn
}

# Retrieve Entry - GET /api/v1/locations/{userId}/{dateTime}
resource "aws_api_gateway_method" "retrieve_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.date_time.id
  http_method   = "GET"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "retrieve_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.date_time.id
  http_method = aws_api_gateway_method.retrieve_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.retrieve_entry_lambda_invoke_arn
}

# Bulk Retrieve Entry - GET /api/v1/locations/{userId}
resource "aws_api_gateway_method" "bulk_retrieve_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.user_id.id
  http_method   = "GET"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "bulk_retrieve_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.user_id.id
  http_method = aws_api_gateway_method.bulk_retrieve_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.bulk_retrieve_entry_lambda_invoke_arn
}

# Delete Entry - DELETE /api/v1/locations/{userId}/{dateTime}
resource "aws_api_gateway_method" "delete_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.date_time.id
  http_method   = "DELETE"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "delete_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.date_time.id
  http_method = aws_api_gateway_method.delete_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.delete_entry_lambda_invoke_arn
}

# Bulk Delete Entry - DELETE /api/v1/locations/{userId}
resource "aws_api_gateway_method" "bulk_delete_entry" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.user_id.id
  http_method   = "DELETE"
  authorization = "CUSTOM"
  authorizer_id = aws_api_gateway_authorizer.custom.id
}

resource "aws_api_gateway_integration" "bulk_delete_entry" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.user_id.id
  http_method = aws_api_gateway_method.bulk_delete_entry.http_method
  
  integration_http_method = "POST"
  type                   = "AWS_PROXY"
  uri                    = var.bulk_delete_entry_lambda_invoke_arn
}

# API Gateway Deployment
resource "aws_api_gateway_deployment" "main" {
  depends_on = [
    aws_api_gateway_integration.options,
    aws_api_gateway_integration_response.options,
    aws_api_gateway_integration.create_entry,
    aws_api_gateway_integration.bulk_create_entry,
    aws_api_gateway_integration.retrieve_entry,
    aws_api_gateway_integration.bulk_retrieve_entry,
    aws_api_gateway_integration.delete_entry,
    aws_api_gateway_integration.bulk_delete_entry
  ]
  
  rest_api_id = aws_api_gateway_rest_api.main.id
  stage_name  = var.environment
  
  lifecycle {
    create_before_destroy = true
  }
}

# API Gateway Stage
resource "aws_api_gateway_stage" "main" {
  deployment_id = aws_api_gateway_deployment.main.id
  rest_api_id   = aws_api_gateway_rest_api.main.id
  stage_name    = var.environment
  
  # Enable CloudWatch logging
  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_gateway.arn
    format = jsonencode({
      requestId      = "$context.requestId"
      ip             = "$context.identity.sourceIp"
      caller         = "$context.identity.caller"
      user           = "$context.identity.user"
      requestTime    = "$context.requestTime"
      httpMethod     = "$context.httpMethod"
      resourcePath   = "$context.resourcePath"
      status         = "$context.status"
      protocol       = "$context.protocol"
      responseLength = "$context.responseLength"
    })
  }
  
  tags = {
    Name = "APIGatewayStage-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# CloudWatch Log Group for API Gateway
resource "aws_cloudwatch_log_group" "api_gateway" {
  name              = "/aws/apigateway/${aws_api_gateway_rest_api.main.name}"
  retention_in_days = 7
  
  tags = {
    Name = "LogGroup-APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# WAF Web ACL
resource "aws_wafv2_web_acl" "api_gateway" {
  name        = "WAF-APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  description = "WAF for API Gateway"
  scope       = "REGIONAL"
  
  default_action {
    allow {}
  }
  
  # Rate limiting rule
  rule {
    name     = "RateLimitRule"
    priority = 1
    
    override_action {
      none {}
    }
    
    statement {
      rate_based_statement {
        limit              = 2000
        aggregate_key_type = "IP"
      }
    }
    
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "RateLimitRule"
      sampled_requests_enabled  = true
    }
  }
  
  # AWS managed rules
  rule {
    name     = "AWSManagedRulesCommonRuleSet"
    priority = 2
    
    override_action {
      none {}
    }
    
    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesCommonRuleSet"
        vendor_name = "AWS"
      }
    }
    
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesCommonRuleSetMetric"
      sampled_requests_enabled  = true
    }
  }
  
  # SQL injection protection
  rule {
    name     = "AWSManagedRulesSQLiRuleSet"
    priority = 3
    
    override_action {
      none {}
    }
    
    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesSQLiRuleSet"
        vendor_name = "AWS"
      }
    }
    
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesSQLiRuleSetMetric"
      sampled_requests_enabled  = true
    }
  }
  
  # XSS protection
  rule {
    name     = "AWSManagedRulesKnownBadInputsRuleSet"
    priority = 4
    
    override_action {
      none {}
    }
    
    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesKnownBadInputsRuleSet"
        vendor_name = "AWS"
      }
    }
    
    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesKnownBadInputsRuleSetMetric"
      sampled_requests_enabled  = true
    }
  }
  
  # Geo restriction (optional)
  dynamic "rule" {
    for_each = var.geo_restriction_countries != [] ? [1] : []
    content {
      name     = "GeoRestrictionRule"
      priority = 5
      
      action {
        block {}
      }
      
      statement {
        geo_match_statement {
          country_codes = var.geo_restriction_countries
        }
      }
      
      visibility_config {
        cloudwatch_metrics_enabled = true
        metric_name               = "GeoRestrictionRule"
        sampled_requests_enabled  = true
      }
    }
  }
  
  # Visibility configuration
  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name               = "WAFAPIGatewayMetric"
    sampled_requests_enabled  = true
  }
  
  tags = {
    Name = "WAF-APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# WAF Web ACL Association
resource "aws_wafv2_web_acl_association" "api_gateway" {
  resource_arn = aws_api_gateway_stage.main.arn
  web_acl_arn  = aws_wafv2_web_acl.api_gateway.arn
}

# CloudWatch Dashboard
resource "aws_cloudwatch_dashboard" "api_gateway" {
  count          = var.enable_cloudwatch_dashboard ? 1 : 0
  dashboard_name = "Dashboard-APIGateway-MTKBackend-${var.aws_region}-${var.availability_zone}"
  
  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6
        
        properties = {
          metrics = [
            ["AWS/ApiGateway", "Count", "ApiName", aws_api_gateway_rest_api.main.name, "Stage", var.environment],
            [".", "4XXError", ".", ".", ".", "."],
            [".", "5XXError", ".", ".", ".", "."]
          ]
          period = 300
          stat   = "Sum"
          region = var.aws_region
          title  = "API Gateway Requests and Errors"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6
        
        properties = {
          metrics = [
            ["AWS/ApiGateway", "Latency", "ApiName", aws_api_gateway_rest_api.main.name, "Stage", var.environment]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "API Gateway Latency"
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6
        
        properties = {
          metrics = [
            ["AWS/WAFV2", "AllowedRequests", "WebACL", aws_wafv2_web_acl.api_gateway.name, "Region", var.aws_region],
            [".", "BlockedRequests", ".", ".", ".", "."]
          ]
          period = 300
          stat   = "Sum"
          region = var.aws_region
          title  = "WAF Requests"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 12
        height = 6
        
        properties = {
          metrics = [
            ["AWS/Lambda", "Invocations", "FunctionName", "Lambda-CreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"],
            [".", "Errors", ".", "."],
            [".", "Invocations", "FunctionName", "Lambda-RetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"],
            [".", "Errors", ".", "."]
          ]
          period = 300
          stat   = "Sum"
          region = var.aws_region
          title  = "Lambda Function Invocations"
        }
      }
    ]
  })
}

# IAM Role for API Gateway CloudWatch
resource "aws_iam_role" "api_gateway_cloudwatch" {
  name = "IAMRole-APIGatewayCloudWatch-MTKBackend-${var.aws_region}-${var.availability_zone}"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "apigateway.amazonaws.com"
        }
      }
    ]
  })
  
  tags = {
    Name = "IAMRole-APIGatewayCloudWatch-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_iam_role_policy" "api_gateway_cloudwatch" {
  name = "IAMPolicy-APIGatewayCloudWatch-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role = aws_iam_role.api_gateway_cloudwatch.id
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams",
          "logs:PutLogEvents",
          "logs:GetLogEvents",
          "logs:FilterLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

# API Gateway Account
resource "aws_api_gateway_account" "main" {
  cloudwatch_role_arn = aws_iam_role.api_gateway_cloudwatch.arn
} 