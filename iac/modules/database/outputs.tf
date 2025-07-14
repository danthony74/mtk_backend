output "aurora_cluster_id" {
  description = "The ID of the Aurora cluster"
  value       = aws_rds_cluster.aurora.id
}

output "aurora_cluster_endpoint" {
  description = "The endpoint of the Aurora cluster"
  value       = aws_rds_cluster.aurora.endpoint
}

output "aurora_cluster_reader_endpoint" {
  description = "The reader endpoint of the Aurora cluster"
  value       = aws_rds_cluster.aurora.reader_endpoint
}

output "aurora_instance_ids" {
  description = "List of Aurora instance IDs"
  value       = aws_rds_cluster_instance.aurora[*].id
}

output "database_subnet_group_name" {
  description = "The name of the database subnet group"
  value       = aws_db_subnet_group.aurora.name
}

output "rds_monitoring_role_arn" {
  description = "The ARN of the RDS monitoring role"
  value       = aws_iam_role.rds_monitoring.arn
} 