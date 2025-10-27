#!/bin/bash

# Setup script for LAB04 Custom Progress Bar
# This script helps configure the correct Java version for building

echo "=== LAB04 Custom Progress Bar Setup ==="
echo ""

# Check current Java version
echo "Current Java version:"
java -version
echo ""

# Check available Java versions
echo "Available Java versions:"
/usr/libexec/java_home -V 2>/dev/null || echo "No Java versions found via /usr/libexec/java_home"
echo ""

# Try to set Java 21 if available
echo "Attempting to set Java 21..."
if /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 21)
    echo "✅ Java 21 found and set"
    echo "JAVA_HOME: $JAVA_HOME"
    java -version
else
    echo "❌ Java 21 not found"
    echo ""
    echo "Please install Java 21:"
    echo "brew install openjdk@21"
    echo ""
    echo "Or use Android Studio to build the project"
fi

echo ""
echo "=== Build Instructions ==="
echo "1. Make sure you have Java 21 installed"
echo "2. Run: ./gradlew clean assembleDebug"
echo "3. Install: adb install app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "=== Alternative: Use Android Studio ==="
echo "1. Open project in Android Studio"
echo "2. Set JDK to Java 21 in Project Structure"
echo "3. Build and run the project"
echo ""

# Try building if Java 21 is available
if /usr/libexec/java_home -v 21 >/dev/null 2>&1; then
    echo "Attempting to build..."
    ./gradlew clean assembleDebug
else
    echo "Skipping build - Java 21 not available"
fi
