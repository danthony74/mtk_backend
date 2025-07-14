variable "environment" {
  description = "Environment name (dev, qa, prod)"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "availability_zone" {
  description = "Primary availability zone"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
}

variable "db_instance_class" {
  description = "Aurora PostgreSQL instance class"
  type        = string
  default     = "db.r6g.large"
}

variable "db_cluster_size" {
  description = "Number of Aurora instances in cluster"
  type        = number
  default     = 2
}

variable "db_engine_version" {
  description = "Aurora PostgreSQL engine version"
  type        = string
  default     = "15.4"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "mtk_backend"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "mtk_admin"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "backup_retention_period" {
  description = "Backup retention period in days"
  type        = number
  default     = 7
}

variable "deletion_protection" {
  description = "Enable deletion protection"
  type        = bool
  default     = false
}

variable "enable_encryption" {
  description = "Enable encryption at rest"
  type        = bool
  default     = true
}

variable "enable_cloudwatch_logs" {
  description = "Enable CloudWatch logs"
  type        = bool
  default     = true
}

variable "dynamodb_billing_mode" {
  description = "DynamoDB billing mode"
  type        = string
  default     = "PAY_PER_REQUEST"
}

variable "dynamodb_point_in_time_recovery" {
  description = "Enable point-in-time recovery for DynamoDB"
  type        = bool
  default     = true
}

variable "enable_global_tables" {
  description = "Enable DynamoDB global tables"
  type        = bool
  default     = true
}

variable "secondary_region" {
  description = "Secondary region for global tables"
  type        = string
  default     = ""
}

variable "ipam_pool_cidr" {
  description = "IPAM pool CIDR block"
  type        = string
}

variable "ipam_pool_description" {
  description = "IPAM pool description"
  type        = string
  default     = "MTK Backend IPAM Pool"
}

# API Gateway Variables
variable "api_gateway_callback_urls" {
  description = "List of callback URLs for Cognito OAuth"
  type        = list(string)
  default     = ["http://localhost:3000/callback"]
}

variable "api_gateway_logout_urls" {
  description = "List of logout URLs for Cognito OAuth"
  type        = list(string)
  default     = ["http://localhost:3000/logout"]
}

variable "api_gateway_enable_lambda_triggers" {
  description = "Enable Lambda triggers for Cognito"
  type        = bool
  default     = false
}

variable "api_gateway_pre_authentication_lambda_arn" {
  description = "ARN of the pre-authentication Lambda function"
  type        = string
  default     = ""
}

variable "api_gateway_pre_token_generation_lambda_arn" {
  description = "ARN of the pre-token generation Lambda function"
  type        = string
  default     = ""
}

variable "api_gateway_user_migration_lambda_arn" {
  description = "ARN of the user migration Lambda function"
  type        = string
  default     = ""
}

variable "api_gateway_geo_restriction_countries" {
  description = "List of country codes to block (empty for no geo restriction)"
  type        = list(string)
  default     = []
}

variable "api_gateway_waf_rate_limit" {
  description = "Rate limit for WAF (requests per 5 minutes)"
  type        = number
  default     = 2000
}

variable "api_gateway_enable_waf_managed_rules" {
  description = "Enable AWS managed WAF rules"
  type        = bool
  default     = true
}

variable "api_gateway_enable_waf_rate_limiting" {
  description = "Enable WAF rate limiting"
  type        = bool
  default     = true
}

variable "api_gateway_enable_waf_geo_restriction" {
  description = "Enable WAF geo restriction"
  type        = bool
  default     = false
}

variable "api_gateway_log_retention_days" {
  description = "Number of days to retain API Gateway logs"
  type        = number
  default     = 7
}

variable "api_gateway_enable_cloudwatch_dashboard" {
  description = "Enable CloudWatch dashboard for monitoring"
  type        = bool
  default     = true
}

# Lambda Variables
variable "lambda_jar_path" {
  description = "Path to the Lambda JAR file"
  type        = string
  default     = "src/lambda/cqrs/target/cqrs-lambda-1.0.0.jar"
}
  type        = bool
  default     = true
} 