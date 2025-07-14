output "dynamodb_table_name" {
  description = "The name of the DynamoDB table"
  value       = aws_dynamodb_table.user_locations.name
}

output "dynamodb_table_arn" {
  description = "The ARN of the DynamoDB table"
  value       = aws_dynamodb_table.user_locations.arn
}

output "dynamodb_table_id" {
  description = "The ID of the DynamoDB table"
  value       = aws_dynamodb_table.user_locations.id
}

output "dynamodb_stream_table_name" {
  description = "The name of the DynamoDB stream table (if enabled)"
  value       = var.enable_global_tables ? aws_dynamodb_table.user_locations_with_stream[0].name : null
}

output "dynamodb_stream_table_arn" {
  description = "The ARN of the DynamoDB stream table (if enabled)"
  value       = var.enable_global_tables ? aws_dynamodb_table.user_locations_with_stream[0].arn : null
} 