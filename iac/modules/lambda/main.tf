# Lambda Functions for CQRS Operations

# IAM Role for Lambda functions
resource "aws_iam_role" "lambda_role" {
  name = "IAMRole-Lambda-MTKBackend-${var.aws_region}-${var.availability_zone}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "IAMRole-Lambda-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# IAM Policy for Lambda functions
resource "aws_iam_role_policy" "lambda_policy" {
  name = "IAMPolicy-Lambda-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:${var.aws_region}:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "dynamodb:BatchGetItem",
          "dynamodb:BatchWriteItem"
        ]
        Resource = [
          var.dynamodb_table_arn,
          "${var.dynamodb_table_arn}/index/*"
        ]
      }
    ]
  })
}

# CloudWatch Log Groups for each Lambda function
resource "aws_cloudwatch_log_group" "create_entry_logs" {
  name              = "/aws/lambda/Lambda-CreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-CreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "bulk_create_entry_logs" {
  name              = "/aws/lambda/Lambda-BulkCreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-BulkCreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "retrieve_entry_logs" {
  name              = "/aws/lambda/Lambda-RetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-RetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "bulk_retrieve_entry_logs" {
  name              = "/aws/lambda/Lambda-BulkRetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-BulkRetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "delete_entry_logs" {
  name              = "/aws/lambda/Lambda-DeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-DeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "bulk_delete_entry_logs" {
  name              = "/aws/lambda/Lambda-BulkDeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-BulkDeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_cloudwatch_log_group" "cognito_authorizer_logs" {
  name              = "/aws/lambda/Lambda-CognitoAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  retention_in_days = 14

  tags = {
    Name = "LogGroup-CognitoAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# Lambda Function - Create Entry
resource "aws_lambda_function" "create_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-CreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.commands.CreateEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 30
  memory_size     = 512

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-CreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.create_entry_logs]
}

# Lambda Function - Bulk Create Entry
resource "aws_lambda_function" "bulk_create_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-BulkCreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.commands.BulkCreateEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 60
  memory_size     = 1024

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-BulkCreateEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.bulk_create_entry_logs]
}

# Lambda Function - Retrieve Entry
resource "aws_lambda_function" "retrieve_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-RetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.queries.RetrieveEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 30
  memory_size     = 512

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-RetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.retrieve_entry_logs]
}

# Lambda Function - Bulk Retrieve Entry
resource "aws_lambda_function" "bulk_retrieve_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-BulkRetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.queries.BulkRetrieveEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 60
  memory_size     = 1024

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-BulkRetrieveEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.bulk_retrieve_entry_logs]
}

# Lambda Function - Delete Entry
resource "aws_lambda_function" "delete_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-DeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.commands.DeleteEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 30
  memory_size     = 512

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-DeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.delete_entry_logs]
}

# Lambda Function - Bulk Delete Entry
resource "aws_lambda_function" "bulk_delete_entry" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-BulkDeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.commands.BulkDeleteEntryHandler::handleRequest"
  runtime         = "java11"
  timeout         = 60
  memory_size     = 1024

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = var.dynamodb_table_name
      ENVIRONMENT         = var.environment
      AWS_REGION          = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-BulkDeleteEntry-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.bulk_delete_entry_logs]
}

# Lambda Function - Cognito Authorizer
resource "aws_lambda_function" "cognito_authorizer" {
  filename         = var.lambda_jar_path
  function_name    = "Lambda-CognitoAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  role            = aws_iam_role.lambda_role.arn
  handler         = "com.mindthekid.geo.cqrs.infrastructure.CognitoAuthorizer::handleRequest"
  runtime         = "java11"
  timeout         = 30
  memory_size     = 512

  environment {
    variables = {
      COGNITO_USER_POOL_ID = var.cognito_user_pool_id
      ENVIRONMENT          = var.environment
      AWS_REGION           = var.aws_region
    }
  }

  tags = {
    Name = "Lambda-CognitoAuthorizer-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }

  depends_on = [aws_cloudwatch_log_group.cognito_authorizer_logs]
}

# Lambda Permission for API Gateway integration
resource "aws_lambda_permission" "create_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.create_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "bulk_create_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.bulk_create_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "retrieve_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.retrieve_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "bulk_retrieve_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.bulk_retrieve_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "delete_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.delete_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "bulk_delete_entry_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.bulk_delete_entry.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
}

resource "aws_lambda_permission" "cognito_authorizer_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.cognito_authorizer.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_gateway_execution_arn}/*/*"
} 