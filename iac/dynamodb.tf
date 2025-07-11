# DynamoDB Table
resource "aws_dynamodb_table" "user_locations" {
  name           = "${var.environment}-${var.aws_region}-user-locations"
  billing_mode   = var.dynamodb_billing_mode
  hash_key       = "user_id"
  range_key      = "date_time"
  
  # Point-in-time recovery
  point_in_time_recovery {
    enabled = var.dynamodb_point_in_time_recovery
  }
  
  # Server-side encryption
  server_side_encryption {
    enabled = true
  }
  
  # Attribute definitions
  attribute {
    name = "user_id"
    type = "S"
  }
  
  attribute {
    name = "date_time"
    type = "S"
  }
  
  # Global Secondary Index for location queries
  global_secondary_index {
    name            = "lat_long_index"
    hash_key        = "lat_long"
    range_key       = "date_time"
    projection_type = "ALL"
  }
  
  # Global Secondary Index for privacy queries
  global_secondary_index {
    name            = "is_private_index"
    hash_key        = "is_private"
    range_key       = "date_time"
    projection_type = "ALL"
  }
  
  # Global Secondary Index for real data queries
  global_secondary_index {
    name            = "is_real_index"
    hash_key        = "is_real"
    range_key       = "date_time"
    projection_type = "ALL"
  }
  
  # Tags
  tags = {
    Name = "${var.environment}_${var.aws_region}_user_locations_table"
  }
}

# DynamoDB Global Table (if enabled and secondary region specified)
resource "aws_dynamodb_global_table" "user_locations" {
  count = var.enable_global_tables && var.secondary_region != "" ? 1 : 0
  
  name = aws_dynamodb_table.user_locations.name
  
  replica {
    region_name = var.aws_region
  }
  
  replica {
    region_name = var.secondary_region
  }
  
  depends_on = [aws_dynamodb_table.user_locations]
}

# DynamoDB Auto Scaling for read capacity (if using provisioned billing)
resource "aws_appautoscaling_target" "dynamodb_table_read_target" {
  count              = var.dynamodb_billing_mode == "PROVISIONED" ? 1 : 0
  max_capacity       = 100
  min_capacity       = 5
  resource_id        = "table/${aws_dynamodb_table.user_locations.name}"
  scalable_dimension = "dynamodb:table:ReadCapacityUnits"
  service_namespace  = "dynamodb"
}

resource "aws_appautoscaling_policy" "dynamodb_table_read_policy" {
  count              = var.dynamodb_billing_mode == "PROVISIONED" ? 1 : 0
  name               = "DynamoDBReadCapacityUtilization:${aws_appautoscaling_target.dynamodb_table_read_target[0].resource_id}"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.dynamodb_table_read_target[0].resource_id
  scalable_dimension = aws_appautoscaling_target.dynamodb_table_read_target[0].scalable_dimension
  service_namespace  = aws_appautoscaling_target.dynamodb_table_read_target[0].service_namespace
  
  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "DynamoDBReadCapacityUtilization"
    }
    target_value = 70.0
  }
}

# DynamoDB Auto Scaling for write capacity (if using provisioned billing)
resource "aws_appautoscaling_target" "dynamodb_table_write_target" {
  count              = var.dynamodb_billing_mode == "PROVISIONED" ? 1 : 0
  max_capacity       = 100
  min_capacity       = 5
  resource_id        = "table/${aws_dynamodb_table.user_locations.name}"
  scalable_dimension = "dynamodb:table:WriteCapacityUnits"
  service_namespace  = "dynamodb"
}

resource "aws_appautoscaling_policy" "dynamodb_table_write_policy" {
  count              = var.dynamodb_billing_mode == "PROVISIONED" ? 1 : 0
  name               = "DynamoDBWriteCapacityUtilization:${aws_appautoscaling_target.dynamodb_table_write_target[0].resource_id}"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.dynamodb_table_write_target[0].resource_id
  scalable_dimension = aws_appautoscaling_target.dynamodb_table_write_target[0].scalable_dimension
  service_namespace  = aws_appautoscaling_target.dynamodb_table_write_target[0].service_namespace
  
  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "DynamoDBWriteCapacityUtilization"
    }
    target_value = 70.0
  }
}

# DynamoDB Stream (for global tables and change tracking)
resource "aws_dynamodb_table" "user_locations_with_stream" {
  count          = var.enable_global_tables ? 1 : 0
  name           = "${var.environment}-${var.aws_region}-user-locations-stream"
  billing_mode   = var.dynamodb_billing_mode
  hash_key       = "user_id"
  range_key      = "date_time"
  
  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"
  
  # Point-in-time recovery
  point_in_time_recovery {
    enabled = var.dynamodb_point_in_time_recovery
  }
  
  # Server-side encryption
  server_side_encryption {
    enabled = true
  }
  
  # Attribute definitions
  attribute {
    name = "user_id"
    type = "S"
  }
  
  attribute {
    name = "date_time"
    type = "S"
  }
  
  # Tags
  tags = {
    Name = "${var.environment}_${var.aws_region}_user_locations_stream_table"
  }
} 