#!/bin/bash

# Spam Call Blocker - Build Script
echo "Building Spam Call Blocker Android App..."

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "Error: ANDROID_HOME environment variable not set"
    echo "Please install Android SDK and set ANDROID_HOME"
    exit 1
fi

# Create gradlew if it doesn't exist
if [ ! -f "gradlew" ]; then
    echo "Creating Gradle wrapper..."
    gradle wrapper
fi

# Make gradlew executable
chmod +x gradlew

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
    
    # Show APK info
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        echo "APK size: $(du -h "$APK_PATH" | cut -f1)"
        echo ""
        echo "To install on device:"
        echo "adb install $APK_PATH"
    fi
else
    echo "❌ Build failed!"
    exit 1
fi