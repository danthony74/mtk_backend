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

variable "callback_urls" {
  description = "List of callback URLs for Cognito OAuth"
  type        = list(string)
  default     = ["http://localhost:3000/callback"]
}

variable "logout_urls" {
  description = "List of logout URLs for Cognito OAuth"
  type        = list(string)
  default     = ["http://localhost:3000/logout"]
}

variable "enable_lambda_triggers" {
  description = "Enable Lambda triggers for Cognito"
  type        = bool
  default     = false
}

variable "pre_authentication_lambda_arn" {
  description = "ARN of the pre-authentication Lambda function"
  type        = string
  default     = ""
}

variable "pre_token_generation_lambda_arn" {
  description = "ARN of the pre-token generation Lambda function"
  type        = string
  default     = ""
}

variable "user_migration_lambda_arn" {
  description = "ARN of the user migration Lambda function"
  type        = string
  default     = ""
}

variable "geo_restriction_countries" {
  description = "List of country codes to block (empty for no geo restriction)"
  type        = list(string)
  default     = []
}

variable "waf_rate_limit" {
  description = "Rate limit for WAF (requests per 5 minutes)"
  type        = number
  default     = 2000
}

variable "enable_waf_managed_rules" {
  description = "Enable AWS managed WAF rules"
  type        = bool
  default     = true
}

variable "enable_waf_rate_limiting" {
  description = "Enable WAF rate limiting"
  type        = bool
  default     = true
}

variable "enable_waf_geo_restriction" {
  description = "Enable WAF geo restriction"
  type        = bool
  default     = false
}

variable "api_gateway_log_retention_days" {
  description = "Number of days to retain API Gateway logs"
  type        = number
  default     = 7
}

variable "enable_cloudwatch_dashboard" {
  description = "Enable CloudWatch dashboard for monitoring"
  type        = bool
  default     = true
}

# Lambda Integration Variables
variable "create_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Create Entry Lambda function"
  type        = string
  default     = ""
}

variable "bulk_create_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Bulk Create Entry Lambda function"
  type        = string
  default     = ""
}

variable "retrieve_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Retrieve Entry Lambda function"
  type        = string
  default     = ""
}

variable "bulk_retrieve_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Bulk Retrieve Entry Lambda function"
  type        = string
  default     = ""
}

variable "delete_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Delete Entry Lambda function"
  type        = string
  default     = ""
}

variable "bulk_delete_entry_lambda_invoke_arn" {
  description = "Invoke ARN for the Bulk Delete Entry Lambda function"
  type        = string
  default     = ""
}

variable "cognito_authorizer_lambda_invoke_arn" {
  description = "Invoke ARN for the Cognito Authorizer Lambda function"
  type        = string
  default     = ""
} 