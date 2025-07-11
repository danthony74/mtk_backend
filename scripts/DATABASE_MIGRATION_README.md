# Database Version Checker and Migration Script

This script automatically checks an Aurora PostgreSQL database for schema version and applies DDL migrations in the correct order.

## Features

- **Automatic Aurora Discovery**: Uses AWS SDK to find the Aurora cluster endpoint
- **Version Tracking**: Checks the `version_info` table for current database version
- **Sequential Migration**: Executes DDL scripts in version order (v1.0, v1.1, v2.0, etc.)
- **Safe Execution**: Only applies scripts with versions higher than current database version
- **Version Validation**: Verifies script version matches filename and content
- **Comprehensive Logging**: Logs all operations to both console and file

## Prerequisites

1. **Python Dependencies**: Install required packages
   ```bash
   pip install -r requirements.txt
   ```

2. **AWS Credentials**: Configure AWS credentials with RDS and EC2 permissions
   ```bash
   aws configure
   ```

3. **Environment Variable**: Set the database password
   ```bash
   export mtk_db_password="your_database_password"
   ```

## Usage

### Basic Usage
```bash
python scripts/database_version_checker.py
```

### With Environment and Region
```bash
python scripts/database_version_checker.py --environment prod --region us-west-2
```

### Command Line Options
- `--environment`: Environment name (dev, qa, prod) - default: dev
- `--region`: AWS region - default: us-east-1

## How It Works

1. **Database Discovery**: 
   - Queries AWS RDS to find the Aurora cluster endpoint
   - Cluster name format: `{environment}-{region}-aurora-cluster`

2. **Connection Test**:
   - Attempts to connect to the database using `mtk_admin` user
   - Uses password from `mtk_db_password` environment variable

3. **Version Check**:
   - Checks if `version_info` table exists
   - If exists, retrieves `version_number` for `component="database_version"`
   - If not exists or no version found, starts from version 1.0

4. **Script Discovery**:
   - Scans `data/` directory for files matching pattern `mtk_backend_ddl_v*.sql`
   - Extracts version numbers from filenames
   - Sorts scripts by version number

5. **Migration Execution**:
   - Executes scripts with versions higher than current database version
   - Updates `version_info` table after each successful script execution
   - Logs all operations and errors

## DDL Script Format

Scripts should follow this naming convention:
```
mtk_backend_ddl_v1.0.sql
mtk_backend_ddl_v1.1.sql
mtk_backend_ddl_v2.0.sql
```

Each script should include a version comment on the first line:
```sql
#database_version=1.0
CREATE DATABASE "mtk_backend";
-- rest of DDL statements...
```

## Database Schema

The script expects a `version_info` table with this structure:
```sql
CREATE TABLE "version_info" (
    "component" VARCHAR(255) NOT NULL UNIQUE,
    "version_number" DECIMAL(10,3) NOT NULL,
    PRIMARY KEY("component")
);
```

## Logging

The script creates two log outputs:
- **Console**: Real-time progress and status messages
- **File**: `database_migration.log` - Complete log with timestamps

## Error Handling

- **Connection Failures**: Logs error and exits with code 1
- **Script Execution Errors**: Rolls back transaction and logs error
- **Version Mismatches**: Warns but continues execution
- **Missing Dependencies**: Clear error messages for missing requirements

## Security Considerations

- Database password is read from environment variable (not hardcoded)
- Uses AWS IAM roles for authentication
- Database connections use SSL when available
- Scripts are executed in transactions with rollback on failure

## Example Output

```
2024-01-15 10:30:00 - INFO - Starting database migration for environment: dev
2024-01-15 10:30:01 - INFO - Found Aurora cluster endpoint: dev-us-east-1-aurora-cluster.cluster-xyz.us-east-1.rds.amazonaws.com
2024-01-15 10:30:02 - INFO - Database connection successful
2024-01-15 10:30:03 - INFO - Current database version: 1.0
2024-01-15 10:30:04 - INFO - Found 2 DDL scripts: ['v1.0', 'v1.1']
2024-01-15 10:30:04 - INFO - Skipping script version 1.0 (already applied)
2024-01-15 10:30:05 - INFO - Executing script version 1.1
2024-01-15 10:30:06 - INFO - Successfully executed script: data/mtk_backend_ddl_v1.1.sql
2024-01-15 10:30:07 - INFO - Updated database version to 1.1
2024-01-15 10:30:07 - INFO - Successfully migrated to version 1.1
2024-01-15 10:30:07 - INFO - Database migration completed successfully
```

## Troubleshooting

### Common Issues

1. **AWS Credentials Not Configured**
   ```
   Error: Unable to locate credentials
   ```
   Solution: Run `aws configure` and set up your credentials

2. **Database Password Not Set**
   ```
   ValueError: Environment variable 'mtk_db_password' is required
   ```
   Solution: Export the password: `export mtk_db_password="your_password"`

3. **Aurora Cluster Not Found**
   ```
   Aurora cluster 'dev-us-east-1-aurora-cluster' not found
   ```
   Solution: Verify the cluster exists and you have proper permissions

4. **Connection Timeout**
   ```
   Database connection failed: timeout expired
   ```
   Solution: Check network connectivity and security group settings

### Debug Mode

For detailed debugging, you can modify the logging level in the script:
```python
logging.basicConfig(level=logging.DEBUG, ...)
``` 