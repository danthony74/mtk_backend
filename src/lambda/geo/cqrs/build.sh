#!/bin/bash

# Build script for CQRS Lambda functions

set -e

echo "Building CQRS Lambda functions..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 11+ is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Get Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Error: Java 11 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "Using Java version: $(java -version 2>&1 | head -n 1)"

# Clean and build
echo "Cleaning previous builds..."
mvn clean

echo "Compiling and packaging..."
mvn package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful! JAR file created at: target/cqrs-lambda-1.0.0.jar"
    echo "File size: $(du -h target/cqrs-lambda-1.0.0.jar | cut -f1)"
else
    echo "Build failed!"
    exit 1
fi

echo "Build completed successfully!" 