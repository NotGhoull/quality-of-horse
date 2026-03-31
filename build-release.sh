#!/bin/bash
set -e

echo "Building mod JARs..."

# Build with Java 21
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew clean
./gradlew build

# Create release directory
mkdir -p release

# Copy the distribution JARs to release folder
cp fabric/build/libs/qoh-fabric-1.21-*.jar release/
cp forge/build/libs/"Quality of horse-forge-1.21-"*.jar release/
cp neoforge/build/libs/qoh-neoforge-1.21-*.jar release/

echo "Release files created in ./release:"
ls -la release/