#!/bin/bash

# GrindrPlus Build Script
# This script sets up and builds the Android APK

echo "========================================="
echo "  GrindrPlus Build Script"
echo "========================================="
echo ""

# Set up environment variables
export JAVA_HOME=/home/codespace/java/21.0.9-ms
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

echo "✓ Java Home: $JAVA_HOME"
echo "✓ Android Home: $ANDROID_HOME"
echo "✓ Path updated"
echo ""

# Verify Java
echo "Checking Java..."
java -version
echo ""

# Navigate to project
cd /workspaces/GrindrPP
echo "✓ Working directory: $(pwd)"
echo ""

# Create local.properties if not exists
if [ ! -f "local.properties" ]; then
    echo "Creating local.properties..."
    cat > local.properties << EOF
sdk.dir=$ANDROID_HOME
ndk.dir=$ANDROID_HOME/ndk/26.1.10909125
EOF
    echo "✓ local.properties created"
else
    echo "✓ local.properties already exists"
fi
echo ""

# Make gradlew executable
chmod +x gradlew
echo "✓ gradlew is executable"
echo ""

# Download dependencies
echo "Downloading dependencies..."
./gradlew clean --no-daemon 2>&1 | grep -E "(BUILD|:)"
echo ""

# Build debug APK
echo "Building Debug APK..."
echo ""
./gradlew assembleDebug --no-daemon 2>&1 | tail -100

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "========================================="
    echo "  ✓ BUILD SUCCESSFUL!"
    echo "========================================="
    echo ""
    echo "APK Location:"
    ls -lh app/build/outputs/apk/debug/app-debug.apk
    echo ""
    echo "Next Steps:"
    echo "1. Transfer APK to Android device"
    echo "2. Install via LSPosed/LSPatch"
    echo "3. Enable 'BehavioralSimulation' hook"
    echo "4. Enable 'Profile Analytics' hook"
    echo "5. Restart Grindr"
    echo "6. Test: /analytics summary"
    echo ""
else
    echo ""
    echo "========================================="
    echo "  ✗ BUILD FAILED!"
    echo "========================================="
    echo ""
    echo "Check the errors above."
    exit 1
fi
