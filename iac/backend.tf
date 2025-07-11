# Backend Configuration for Remote State Management
# Uncomment and configure for your environment

/*
terraform {
  backend "s3" {
    bucket         = "mtk-backend-terraform-state"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-state-lock"
  }
}
*/

# Alternative: Local state (for development)
# Keep this configuration for local development
# For production, use the S3 backend configuration above 