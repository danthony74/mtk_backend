# VPC Outputs
output "vpc_id" {
  description = "VPC ID"
  value       = module.network.vpc_id
}

output "vpc_cidr" {
  description = "VPC CIDR block"
  value       = module.network.vpc_cidr_block
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value       = module.network.private_subnet_ids
}

output "database_subnet_ids" {
  description = "Database subnet IDs"
  value       = module.network.database_subnet_ids
}

# Aurora PostgreSQL Outputs
output "aurora_cluster_id" {
  description = "Aurora cluster ID"
  value       = module.database.aurora_cluster_id
}

output "aurora_cluster_endpoint" {
  description = "Aurora cluster endpoint"
  value       = module.database.aurora_cluster_endpoint
}

output "aurora_cluster_reader_endpoint" {
  description = "Aurora cluster reader endpoint"
  value       = module.database.aurora_cluster_reader_endpoint
}

output "aurora_instance_ids" {
  description = "Aurora instance IDs"
  value       = module.database.aurora_instance_ids
}

output "aurora_cluster_arn" {
  description = "Aurora cluster ARN"
  value       = module.database.aurora_cluster_id
}

# DynamoDB Outputs
output "dynamodb_table_name" {
  description = "DynamoDB table name"
  value       = module.dynamodb.dynamodb_table_name
}

output "dynamodb_table_arn" {
  description = "DynamoDB table ARN"
  value       = module.dynamodb.dynamodb_table_arn
}

output "dynamodb_table_stream_arn" {
  description = "DynamoDB table stream ARN"
  value       = module.dynamodb.dynamodb_stream_table_arn
}

# IPAM Outputs
output "ipam_id" {
  description = "IPAM ID"
  value       = module.network.vpc_id
}

output "ipam_pool_id" {
  description = "IPAM pool ID"
  value       = module.network.vpc_id
}

# Security Group Outputs
output "database_security_group_id" {
  description = "Database security group ID"
  value       = module.network.database_security_group_id
}

output "application_security_group_id" {
  description = "Application security group ID"
  value       = module.network.application_security_group_id
}

# Connection Information
output "database_connection_info" {
  description = "Database connection information"
  value = {
    host     = module.database.aurora_cluster_endpoint
    port     = 5432
    database = var.db_name
    username = var.db_username
  }
  sensitive = true
}

output "dynamodb_connection_info" {
  description = "DynamoDB connection information"
  value = {
    table_name = module.dynamodb.dynamodb_table_name
    region     = var.aws_region
  }
}

# API Gateway Outputs
output "api_gateway_id" {
  description = "The ID of the API Gateway"
  value       = module.apigateway.api_gateway_id
}

output "api_gateway_invoke_url" {
  description = "The invoke URL of the API Gateway"
  value       = module.apigateway.api_gateway_invoke_url
}

output "api_gateway_stage_arn" {
  description = "The ARN of the API Gateway stage"
  value       = module.apigateway.api_gateway_stage_arn
}

output "cognito_user_pool_id" {
  description = "The ID of the Cognito User Pool"
  value       = module.apigateway.cognito_user_pool_id
}

output "cognito_user_pool_client_id" {
  description = "The ID of the Cognito User Pool Client"
  value       = module.apigateway.cognito_user_pool_client_id
}

output "cognito_user_pool_domain_url" {
  description = "The full URL of the Cognito User Pool domain"
  value       = module.apigateway.cognito_user_pool_domain_url
}

output "api_gateway_authorizer_id" {
  description = "The ID of the API Gateway authorizer"
  value       = module.apigateway.api_gateway_authorizer_id
}

output "waf_web_acl_id" {
  description = "The ID of the WAF Web ACL"
  value       = module.apigateway.waf_web_acl_id
}

output "oauth_configuration" {
  description = "OAuth configuration for the API Gateway"
  value       = module.apigateway.oauth_configuration
}

output "api_endpoints" {
  description = "API Gateway endpoints"
  value       = module.apigateway.api_endpoints
} 