#!/bin/bash
set -e

MOD_VERSION=$(grep "^mod_version=" gradle.properties | cut -d'=' -f2)
MINECRAFT_VERSION=$(grep "^minecraft_version=" gradle.properties | cut -d'=' -f2)

echo "Building mod JARs (mod_version=$MOD_VERSION, minecraft_version=$MINECRAFT_VERSION)..."

export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew build

mkdir -p release

cp fabric/build/libs/qoh-fabric-${MOD_VERSION}+${MINECRAFT_VERSION}-${MOD_VERSION}.jar release/
mv release/qoh-fabric-${MOD_VERSION}+${MINECRAFT_VERSION}-${MOD_VERSION}.jar release/qoh-fabric-${MOD_VERSION}+${MINECRAFT_VERSION}.jar

cp neoforge/build/libs/qoh-neoforge-${MOD_VERSION}+${MINECRAFT_VERSION}-${MOD_VERSION}.jar release/
mv release/qoh-neoforge-${MOD_VERSION}+${MINECRAFT_VERSION}-${MOD_VERSION}.jar release/qoh-neoforge-${MOD_VERSION}+${MINECRAFT_VERSION}.jar

echo "Release files created in ./release:"
ls -la release/