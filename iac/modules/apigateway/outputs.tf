output "api_gateway_id" {
  description = "The ID of the API Gateway"
  value       = aws_api_gateway_rest_api.main.id
}

output "api_gateway_arn" {
  description = "The ARN of the API Gateway"
  value       = aws_api_gateway_rest_api.main.arn
}

output "api_gateway_execution_arn" {
  description = "The execution ARN of the API Gateway"
  value       = aws_api_gateway_rest_api.main.execution_arn
}

output "api_gateway_invoke_url" {
  description = "The invoke URL of the API Gateway"
  value       = "${aws_api_gateway_stage.main.invoke_url}/api/v1"
}

output "api_gateway_stage_arn" {
  description = "The ARN of the API Gateway stage"
  value       = aws_api_gateway_stage.main.arn
}

output "cognito_user_pool_id" {
  description = "The ID of the Cognito User Pool"
  value       = aws_cognito_user_pool.main.id
}

output "user_pool_id" {
  description = "The ID of the Cognito User Pool (alias)"
  value       = aws_cognito_user_pool.main.id
}

output "cognito_user_pool_arn" {
  description = "The ARN of the Cognito User Pool"
  value       = aws_cognito_user_pool.main.arn
}

output "cognito_user_pool_client_id" {
  description = "The ID of the Cognito User Pool Client"
  value       = aws_cognito_user_pool_client.main.id
}

output "cognito_user_pool_domain" {
  description = "The domain of the Cognito User Pool"
  value       = aws_cognito_user_pool_domain.main.domain
}

output "cognito_user_pool_domain_url" {
  description = "The full URL of the Cognito User Pool domain"
  value       = "https://${aws_cognito_user_pool_domain.main.domain}.auth.${var.aws_region}.amazoncognito.com"
}

output "api_gateway_authorizer_id" {
  description = "The ID of the API Gateway authorizer"
  value       = aws_api_gateway_authorizer.custom.id
}

output "waf_web_acl_id" {
  description = "The ID of the WAF Web ACL"
  value       = aws_wafv2_web_acl.api_gateway.id
}

output "waf_web_acl_arn" {
  description = "The ARN of the WAF Web ACL"
  value       = aws_wafv2_web_acl.api_gateway.arn
}

output "cloudwatch_dashboard_name" {
  description = "The name of the CloudWatch dashboard"
  value       = aws_cloudwatch_dashboard.api_gateway.dashboard_name
}

output "cloudwatch_log_group_name" {
  description = "The name of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.api_gateway.name
}

output "api_gateway_cloudwatch_role_arn" {
  description = "The ARN of the API Gateway CloudWatch role"
  value       = aws_iam_role.api_gateway_cloudwatch.arn
}

output "oauth_configuration" {
  description = "OAuth configuration for the API Gateway"
  value = {
    user_pool_id     = aws_cognito_user_pool.main.id
    client_id        = aws_cognito_user_pool_client.main.id
    domain           = aws_cognito_user_pool_domain.main.domain
    domain_url       = "https://${aws_cognito_user_pool_domain.main.domain}.auth.${var.aws_region}.amazoncognito.com"
    callback_urls    = var.callback_urls
    logout_urls      = var.logout_urls
    allowed_scopes   = ["phone", "email", "openid", "profile"]
  }
}

output "api_endpoints" {
  description = "API Gateway endpoints"
  value = {
    base_url     = aws_api_gateway_stage.main.invoke_url
    api_v1_url   = "${aws_api_gateway_stage.main.invoke_url}/api/v1"
    stage_name   = aws_api_gateway_stage.main.stage_name
    region       = var.aws_region
  }
} 