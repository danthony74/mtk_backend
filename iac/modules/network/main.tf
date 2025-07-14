# IPAM Configuration
resource "aws_vpc_ipam" "main" {
  description = "MTK Backend IPAM"
  
  tags = {
    Name = "IPAM-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_vpc_ipam_pool" "main" {
  address_family = "ipv4"
  ipam_scope_id  = aws_vpc_ipam.main.private_default_scope_id
  name           = "IPAMPool-MTKBackend-${var.aws_region}-${var.availability_zone}"
  description    = var.ipam_pool_description
  
  source_default = true
  
  tags = {
    Name = "IPAMPool-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_vpc_ipam_pool_cidr" "main" {
  ipam_pool_id = aws_vpc_ipam_pool.main.id
  cidr         = var.ipam_pool_cidr
}

# VPC
resource "aws_vpc" "main" {
  ipv4_ipam_pool_id   = aws_vpc_ipam_pool.main.id
  ipv4_netmask_length = 16
  
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name = "VPC-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  
  tags = {
    Name = "IGW-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# Public Subnets
resource "aws_subnet" "public" {
  count             = length(var.public_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.public_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  
  map_public_ip_on_launch = true
  
  tags = {
    Name = "Subnet-Public-MTKBackend-${var.aws_region}-${data.aws_availability_zones.available.names[count.index]}"
  }
}

# Private Subnets
resource "aws_subnet" "private" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  
  tags = {
    Name = "Subnet-Private-MTKBackend-${var.aws_region}-${data.aws_availability_zones.available.names[count.index]}"
  }
}

# Database Subnets
resource "aws_subnet" "database" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 10)
  availability_zone = data.aws_availability_zones.available.names[count.index]
  
  tags = {
    Name = "Subnet-Database-MTKBackend-${var.aws_region}-${data.aws_availability_zones.available.names[count.index]}"
  }
}

# Route Tables
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  
  tags = {
    Name = "RouteTable-Public-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id
  
  tags = {
    Name = "RouteTable-Private-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

# Route Table Associations
resource "aws_route_table_association" "public" {
  count          = length(var.public_subnet_cidrs)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  count          = length(var.private_subnet_cidrs)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "database" {
  count          = length(var.private_subnet_cidrs)
  subnet_id      = aws_subnet.database[count.index].id
  route_table_id = aws_route_table.private.id
}

# Security Groups
resource "aws_security_group" "database" {
  name_prefix = "SecurityGroup-Database-MTKBackend-${var.aws_region}-${var.availability_zone}-"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.application.id]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "SecurityGroup-Database-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
}

resource "aws_security_group" "application" {
  name_prefix = "SecurityGroup-Application-MTKBackend-${var.aws_region}-${var.availability_zone}-"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "SecurityGroup-Application-MTKBackend-${var.aws_region}-${var.availability_zone}"
  }
} 