# MTK Backend Terraform Management Scripts

This directory contains Python scripts for managing Terraform infrastructure for the MTK Backend project across different environments (dev, qa, prod).

## Scripts Overview

### 1. `terraform_manager.py` - Core Terraform Operations
The main script for basic Terraform operations with environment-specific configurations.

**Features:**
- Initialize, validate, plan, apply, and destroy Terraform configurations
- Get current state and outputs
- Refresh state and import resources
- Workspace management
- Environment-specific variable file handling

### 2. `terraform_deploy.py` - Complete Deployment Workflow
A comprehensive deployment script that handles the complete deployment process with safety checks.

**Features:**
- Prerequisites validation (Terraform, AWS CLI, AWS services)
- Complete deployment workflow (init → validate → plan → apply → verify)
- Infrastructure verification
- Deployment information retrieval
- Safe destruction workflow

### 3. `terraform_monitor.py` - Infrastructure Monitoring
Monitor the health and status of deployed infrastructure.

**Features:**
- Resource status checking (VPC, Aurora, DynamoDB, Security Groups)
- Connectivity testing
- CloudWatch metrics retrieval
- Comprehensive health reports
- JSON output for integration with other tools

## Prerequisites

### System Requirements
- **Python 3.7+**
- **Terraform 1.0+**
- **AWS CLI** configured with appropriate credentials
- **AWS Access** to required services (EC2, RDS, DynamoDB, IAM, CloudWatch)

### Installation

1. **Install Python dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

2. **Verify Terraform installation:**
   ```bash
   terraform --version
   ```

3. **Verify AWS CLI configuration:**
   ```bash
   aws sts get-caller-identity
   ```

## Usage Examples

### Basic Terraform Operations

**Initialize Terraform:**
```bash
python terraform_manager.py dev init
```

**Validate configuration:**
```bash
python terraform_manager.py qa validate
```

**Create a plan:**
```bash
python terraform_manager.py prod plan --detailed
```

**Apply changes:**
```bash
python terraform_manager.py dev apply --auto-approve
```

**Get current state:**
```bash
python terraform_manager.py qa get-state
```

**Get outputs:**
```bash
python terraform_manager.py prod get-outputs
```

**Destroy infrastructure:**
```bash
python terraform_manager.py dev destroy --auto-approve
```

### Complete Deployment Workflow

**Deploy to development:**
```bash
python terraform_deploy.py dev deploy
```

**Deploy to QA with auto-approval:**
```bash
python terraform_deploy.py qa deploy --auto-approve
```

**Deploy to production with detailed output:**
```bash
python terraform_deploy.py prod deploy --detailed
```

**Get deployment information:**
```bash
python terraform_deploy.py qa info
```

**Validate configuration only:**
```bash
python terraform_deploy.py dev validate
```

### Infrastructure Monitoring

**Check resource status:**
```bash
python terraform_monitor.py dev status
```

**Test connectivity:**
```bash
python terraform_monitor.py qa connectivity
```

**Get CloudWatch metrics:**
```bash
python terraform_monitor.py prod metrics
```

**Generate comprehensive health report:**
```bash
python terraform_monitor.py dev health-report --output-file health_report.json
```

## Environment Configuration

The scripts automatically use the appropriate variable file based on the environment:

- **Development:** `iac/terraform.tfvars.dev`
- **QA:** `iac/terraform.tfvars.qa`
- **Production:** `iac/terraform.tfvars.prod`

## Script Options

### Common Options
- `--terraform-dir`: Directory containing Terraform files (default: `iac`)
- `--auto-approve`: Skip interactive approval prompts
- `--detailed`: Show detailed output for plans and operations

### Monitor-Specific Options
- `--output-file`: Save output to a JSON file

## Logging

All scripts generate detailed logs:

- **Console output:** Real-time logging to stdout
- **Log files:** Detailed logs saved to script-specific files:
  - `terraform_manager.log`
  - `terraform_deploy.log`
  - `terraform_monitor.log`

## Error Handling

The scripts include comprehensive error handling:

- **Prerequisites validation:** Check for required tools and permissions
- **Command timeouts:** 5-minute timeout for long-running operations
- **Graceful failures:** Proper error messages and exit codes
- **State validation:** Verify infrastructure state before operations

## Security Considerations

- **Credentials:** Use AWS IAM roles or configure AWS CLI with appropriate permissions
- **Auto-approval:** Use `--auto-approve` carefully, especially in production
- **State files:** Consider using remote state storage for production environments
- **Logs:** Review log files for sensitive information before sharing

## Integration with CI/CD

These scripts can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Deploy to Development
  run: |
    python scripts/terraform_deploy.py dev deploy --auto-approve

- name: Run Health Check
  run: |
    python scripts/terraform_monitor.py dev health-report --output-file health.json

- name: Deploy to Production
  if: github.ref == 'refs/heads/main'
  run: |
    python scripts/terraform_deploy.py prod deploy --detailed
```

## Troubleshooting

### Common Issues

1. **Terraform not found:**
   ```bash
   # Install Terraform
   brew install terraform  # macOS
   # or download from https://terraform.io
   ```

2. **AWS credentials not configured:**
   ```bash
   aws configure
   # or set environment variables
   export AWS_ACCESS_KEY_ID=your_key
   export AWS_SECRET_ACCESS_KEY=your_secret
   ```

3. **Permission denied:**
   - Ensure AWS credentials have appropriate permissions
   - Check IAM policies for required services

4. **Variable file not found:**
   - Ensure you're in the correct directory
   - Check that `iac/terraform.tfvars.{env}` files exist

### Debug Mode

For detailed debugging, you can modify the logging level in the scripts:

```python
logging.basicConfig(level=logging.DEBUG)
```

## Contributing

When adding new features to the scripts:

1. Follow the existing code structure
2. Add appropriate error handling
3. Include logging for all operations
4. Update this README with new examples
5. Test with all environments (dev, qa, prod)

## Support

For issues or questions:

1. Check the log files for detailed error information
2. Verify prerequisites are met
3. Test with a simple operation first
4. Review AWS service permissions
5. Contact the infrastructure team 