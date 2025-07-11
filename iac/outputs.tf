# VPC Outputs
output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "VPC CIDR block"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value       = aws_subnet.private[*].id
}

output "database_subnet_ids" {
  description = "Database subnet IDs"
  value       = aws_subnet.database[*].id
}

# Aurora PostgreSQL Outputs
output "aurora_cluster_id" {
  description = "Aurora cluster ID"
  value       = aws_rds_cluster.aurora.id
}

output "aurora_cluster_endpoint" {
  description = "Aurora cluster endpoint"
  value       = aws_rds_cluster.aurora.endpoint
}

output "aurora_cluster_reader_endpoint" {
  description = "Aurora cluster reader endpoint"
  value       = aws_rds_cluster.aurora.reader_endpoint
}

output "aurora_instance_ids" {
  description = "Aurora instance IDs"
  value       = aws_rds_cluster_instance.aurora[*].id
}

output "aurora_cluster_arn" {
  description = "Aurora cluster ARN"
  value       = aws_rds_cluster.aurora.arn
}

# DynamoDB Outputs
output "dynamodb_table_name" {
  description = "DynamoDB table name"
  value       = aws_dynamodb_table.user_locations.name
}

output "dynamodb_table_arn" {
  description = "DynamoDB table ARN"
  value       = aws_dynamodb_table.user_locations.arn
}

output "dynamodb_table_stream_arn" {
  description = "DynamoDB table stream ARN"
  value       = var.enable_global_tables ? aws_dynamodb_table.user_locations_with_stream[0].stream_arn : null
}

# IPAM Outputs
output "ipam_id" {
  description = "IPAM ID"
  value       = aws_vpc_ipam.main.id
}

output "ipam_pool_id" {
  description = "IPAM pool ID"
  value       = aws_vpc_ipam_pool.main.id
}

# Security Group Outputs
output "database_security_group_id" {
  description = "Database security group ID"
  value       = aws_security_group.database.id
}

output "application_security_group_id" {
  description = "Application security group ID"
  value       = aws_security_group.application.id
}

# Connection Information
output "database_connection_info" {
  description = "Database connection information"
  value = {
    host     = aws_rds_cluster.aurora.endpoint
    port     = 5432
    database = var.db_name
    username = var.db_username
  }
  sensitive = true
}

output "dynamodb_connection_info" {
  description = "DynamoDB connection information"
  value = {
    table_name = aws_dynamodb_table.user_locations.name
    region     = var.aws_region
  }
} 