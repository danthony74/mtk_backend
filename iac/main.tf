terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Environment = var.environment
      Region      = var.aws_region
      Zone        = var.availability_zone
      Type        = "infrastructure"
      Project     = "MTKBackend"
      ManagedBy   = "terraform"
      ServiceType = "Backend"
      Purpose     = "MTKBackend"
    }
  }
}

# Data sources
data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

# Network Module
module "network" {
  source = "./modules/network"
  
  environment           = var.environment
  aws_region           = var.aws_region
  availability_zone    = var.availability_zone
  vpc_cidr             = var.vpc_cidr
  private_subnet_cidrs = var.private_subnet_cidrs
  public_subnet_cidrs  = var.public_subnet_cidrs
  ipam_pool_cidr       = var.ipam_pool_cidr
  ipam_pool_description = var.ipam_pool_description
}

# Database Module
module "database" {
  source = "./modules/database"
  
  environment                = var.environment
  aws_region                = var.aws_region
  availability_zone         = var.availability_zone
  db_instance_class         = var.db_instance_class
  db_cluster_size           = var.db_cluster_size
  db_engine_version         = var.db_engine_version
  db_name                   = var.db_name
  db_username               = var.db_username
  db_password               = var.db_password
  backup_retention_period   = var.backup_retention_period
  deletion_protection       = var.deletion_protection
  enable_encryption         = var.enable_encryption
  enable_cloudwatch_logs    = var.enable_cloudwatch_logs
  database_security_group_id = module.network.database_security_group_id
  database_subnet_ids       = module.network.database_subnet_ids
  
  depends_on = [module.network]
}

# DynamoDB Module
module "dynamodb" {
  source = "./modules/dynamodb"
  
  environment                    = var.environment
  aws_region                    = var.aws_region
  availability_zone             = var.availability_zone
  dynamodb_billing_mode         = var.dynamodb_billing_mode
  dynamodb_point_in_time_recovery = var.dynamodb_point_in_time_recovery
  enable_global_tables          = var.enable_global_tables
  secondary_region              = var.secondary_region
}

# API Gateway Module
module "apigateway" {
  source = "./modules/apigateway"
  
  environment                    = var.environment
  aws_region                    = var.aws_region
  availability_zone             = var.availability_zone
  callback_urls                 = var.api_gateway_callback_urls
  logout_urls                   = var.api_gateway_logout_urls
  enable_lambda_triggers        = var.api_gateway_enable_lambda_triggers
  pre_authentication_lambda_arn = var.api_gateway_pre_authentication_lambda_arn
  pre_token_generation_lambda_arn = var.api_gateway_pre_token_generation_lambda_arn
  user_migration_lambda_arn     = var.api_gateway_user_migration_lambda_arn
  geo_restriction_countries     = var.api_gateway_geo_restriction_countries
  waf_rate_limit                = var.api_gateway_waf_rate_limit
  enable_waf_managed_rules      = var.api_gateway_enable_waf_managed_rules
  enable_waf_rate_limiting      = var.api_gateway_enable_waf_rate_limiting
  enable_waf_geo_restriction    = var.api_gateway_enable_waf_geo_restriction
  api_gateway_log_retention_days = var.api_gateway_log_retention_days
  enable_cloudwatch_dashboard   = var.api_gateway_enable_cloudwatch_dashboard
  
  # Lambda integration ARNs
  create_entry_lambda_invoke_arn       = module.lambda.create_entry_lambda_arn
  bulk_create_entry_lambda_invoke_arn  = module.lambda.bulk_create_entry_lambda_arn
  retrieve_entry_lambda_invoke_arn     = module.lambda.retrieve_entry_lambda_arn
  bulk_retrieve_entry_lambda_invoke_arn = module.lambda.bulk_retrieve_entry_lambda_arn
  delete_entry_lambda_invoke_arn       = module.lambda.delete_entry_lambda_arn
  bulk_delete_entry_lambda_invoke_arn  = module.lambda.bulk_delete_entry_lambda_arn
  cognito_authorizer_lambda_invoke_arn = module.lambda.cognito_authorizer_lambda_arn
  
  depends_on = [module.lambda]
}

# Lambda Module
module "lambda" {
  source = "./modules/lambda"
  
  environment                = var.environment
  aws_region                = var.aws_region
  availability_zone         = var.availability_zone
  lambda_jar_path           = var.lambda_jar_path
  dynamodb_table_name       = module.dynamodb.table_name
  dynamodb_table_arn        = module.dynamodb.table_arn
  api_gateway_execution_arn = module.apigateway.execution_arn
  cognito_user_pool_id      = module.apigateway.user_pool_id
  
  depends_on = [module.dynamodb, module.apigateway]
} 