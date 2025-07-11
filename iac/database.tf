# Aurora PostgreSQL Cluster
resource "aws_rds_cluster" "aurora" {
  cluster_identifier     = "${var.environment}-${var.aws_region}-aurora-cluster"
  engine                = "aurora-postgresql"
  engine_version        = var.db_engine_version
  database_name         = var.db_name
  master_username       = var.db_username
  master_password       = var.db_password
  
  # Multi-AZ configuration
  availability_zones    = slice(data.aws_availability_zones.available.names, 0, var.db_cluster_size)
  
  # Backup and maintenance
  backup_retention_period   = var.backup_retention_period
  preferred_backup_window  = "03:00-04:00"
  preferred_maintenance_window = "sun:04:00-sun:05:00"
  
  # Security
  deletion_protection = var.deletion_protection
  storage_encrypted   = var.enable_encryption
  
  # Network
  db_subnet_group_name   = aws_db_subnet_group.aurora.name
  vpc_security_group_ids = [aws_security_group.database.id]
  
  # Monitoring
  enabled_cloudwatch_logs_exports = var.enable_cloudwatch_logs ? ["postgresql"] : []
  
  # Tags
  tags = {
    Name = "${var.environment}_${var.aws_region}_aurora_cluster"
  }
}

# Aurora PostgreSQL Instances
resource "aws_rds_cluster_instance" "aurora" {
  count               = var.db_cluster_size
  identifier          = "${var.environment}-${var.aws_region}-aurora-${count.index + 1}"
  cluster_identifier  = aws_rds_cluster.aurora.id
  instance_class     = var.db_instance_class
  engine             = aws_rds_cluster.aurora.engine
  engine_version     = aws_rds_cluster.aurora.engine_version
  
  # Auto scaling
  auto_minor_version_upgrade = true
  
  # Performance insights
  performance_insights_enabled = true
  performance_insights_retention_period = 7
  
  # Monitoring
  monitoring_interval = 60
  monitoring_role_arn = aws_iam_role.rds_monitoring.arn
  
  tags = {
    Name = "${var.environment}_${var.aws_region}_aurora_instance_${count.index + 1}"
  }
}

# Database Subnet Group
resource "aws_db_subnet_group" "aurora" {
  name       = "${var.environment}-${var.aws_region}-aurora-subnet-group"
  subnet_ids = aws_subnet.database[*].id
  
  tags = {
    Name = "${var.environment}_${var.aws_region}_aurora_subnet_group"
  }
}

# IAM Role for RDS Monitoring
resource "aws_iam_role" "rds_monitoring" {
  name = "${var.environment}-${var.aws_region}-rds-monitoring-role"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "monitoring.rds.amazonaws.com"
        }
      }
    ]
  })
  
  tags = {
    Name = "${var.environment}_${var.aws_region}_rds_monitoring_role"
  }
}

resource "aws_iam_role_policy_attachment" "rds_monitoring" {
  role       = aws_iam_role.rds_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

# Parameter Group for Aurora PostgreSQL
resource "aws_rds_cluster_parameter_group" "aurora" {
  family = "aurora-postgresql15"
  name   = "${var.environment}-${var.aws_region}-aurora-params"
  
  parameter {
    name  = "log_statement"
    value = "all"
  }
  
  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }
  
  tags = {
    Name = "${var.environment}_${var.aws_region}_aurora_param_group"
  }
}

# Cluster Parameter Group Association
resource "aws_rds_cluster_parameter_group" "aurora_cluster" {
  family = "aurora-postgresql15"
  name   = "${var.environment}-${var.aws_region}-aurora-cluster-params"
  
  parameter {
    name  = "rds.force_ssl"
    value = "1"
  }
  
  tags = {
    Name = "${var.environment}_${var.aws_region}_aurora_cluster_param_group"
  }
} 