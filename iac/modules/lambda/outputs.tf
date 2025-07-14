output "lambda_role_arn" {
  description = "ARN of the Lambda IAM role"
  value       = aws_iam_role.lambda_role.arn
}

output "create_entry_lambda_arn" {
  description = "Invoke ARN of the Create Entry Lambda function"
  value       = aws_lambda_function.create_entry.invoke_arn
}

output "bulk_create_entry_lambda_arn" {
  description = "Invoke ARN of the Bulk Create Entry Lambda function"
  value       = aws_lambda_function.bulk_create_entry.invoke_arn
}

output "retrieve_entry_lambda_arn" {
  description = "Invoke ARN of the Retrieve Entry Lambda function"
  value       = aws_lambda_function.retrieve_entry.invoke_arn
}

output "bulk_retrieve_entry_lambda_arn" {
  description = "Invoke ARN of the Bulk Retrieve Entry Lambda function"
  value       = aws_lambda_function.bulk_retrieve_entry.invoke_arn
}

output "delete_entry_lambda_arn" {
  description = "Invoke ARN of the Delete Entry Lambda function"
  value       = aws_lambda_function.delete_entry.invoke_arn
}

output "bulk_delete_entry_lambda_arn" {
  description = "Invoke ARN of the Bulk Delete Entry Lambda function"
  value       = aws_lambda_function.bulk_delete_entry.invoke_arn
}

output "create_entry_lambda_name" {
  description = "Name of the Create Entry Lambda function"
  value       = aws_lambda_function.create_entry.function_name
}

output "bulk_create_entry_lambda_name" {
  description = "Name of the Bulk Create Entry Lambda function"
  value       = aws_lambda_function.bulk_create_entry.function_name
}

output "retrieve_entry_lambda_name" {
  description = "Name of the Retrieve Entry Lambda function"
  value       = aws_lambda_function.retrieve_entry.function_name
}

output "bulk_retrieve_entry_lambda_name" {
  description = "Name of the Bulk Retrieve Entry Lambda function"
  value       = aws_lambda_function.bulk_retrieve_entry.function_name
}

output "delete_entry_lambda_name" {
  description = "Name of the Delete Entry Lambda function"
  value       = aws_lambda_function.delete_entry.function_name
}

output "bulk_delete_entry_lambda_name" {
  description = "Name of the Bulk Delete Entry Lambda function"
  value       = aws_lambda_function.bulk_delete_entry.function_name
}

output "cognito_authorizer_lambda_arn" {
  description = "Invoke ARN of the Cognito Authorizer Lambda function"
  value       = aws_lambda_function.cognito_authorizer.invoke_arn
}

output "cognito_authorizer_lambda_name" {
  description = "Name of the Cognito Authorizer Lambda function"
  value       = aws_lambda_function.cognito_authorizer.function_name
} 