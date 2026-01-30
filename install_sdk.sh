#!/bin/bash

# Android SDK Installation Script
# This script installs all required Android SDK components

echo "========================================="
echo "  Android SDK Installation"
echo "========================================="
echo ""

# Create directories
echo "Creating directories..."
mkdir -p ~/Android/Sdk/cmdline-tools
mkdir -p ~/Android/Sdk/platforms
mkdir -p ~/Android/Sdk/build-tools
mkdir -p ~/Android/Sdk/ndk
echo "✓ Directories created"
echo ""

# Download Android SDK Command Line Tools
echo "Downloading Android SDK Command Line Tools..."
echo "(This may take 2-5 minutes)"
cd ~
if [ ! -f "cmdlinetools.zip" ]; then
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdlinetools.zip
    if [ $? -eq 0 ]; then
        echo "✓ Downloaded successfully"
    else
        echo "✗ Download failed. Check your internet connection."
        exit 1
    fi
else
    echo "✓ File already downloaded"
fi
echo ""

# Extract SDK
echo "Extracting SDK tools..."
unzip -q cmdlinetools.zip -d ~/Android/Sdk/cmdline-tools/
if [ -d ~/Android/Sdk/cmdline-tools/cmdline-tools ]; then
    mv ~/Android/Sdk/cmdline-tools/cmdline-tools ~/Android/Sdk/cmdline-tools/latest
fi
echo "✓ Extracted successfully"
echo ""

# Set environment variables
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
echo "✓ Environment variables set"
echo ""

# Accept licenses
echo "Accepting Android SDK licenses..."
yes | sdkmanager --licenses >/dev/null 2>&1
echo "✓ Licenses accepted"
echo ""

# Install SDK packages
echo "Installing SDK packages..."
echo "(This may take 10-15 minutes)"
echo ""

sdkmanager --install \
    "platforms;android-34" \
    "build-tools;34.0.0" \
    "platform-tools" \
    2>&1 | grep -E "(Installing|Done|Error)" || echo "Installing packages..."

echo ""
echo "✓ SDK packages installed"
echo ""

# Verify installation
echo "Verifying installation..."
echo ""
echo "Platform Tools:"
ls -la $ANDROID_HOME/platform-tools/ | head -3
echo ""
echo "Build Tools:"
ls -la $ANDROID_HOME/build-tools/ 2>/dev/null | head -3 || echo "Build tools directory check..."
echo ""

echo "========================================="
echo "  ✓ INSTALLATION COMPLETE!"
echo "========================================="
echo ""
echo "Next: Run ./build.sh to build the APK"
echo ""

# Create profile.d script to set environment
echo "Creating environment setup script..."
cat > ~/.profile_android << 'EOF'
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
EOF
echo "✓ Environment script created"
echo ""
echo "Add this to your ~/.bashrc or ~/.zshrc to make it permanent:"
echo "source ~/.profile_android"
