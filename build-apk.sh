#!/bin/bash

# Spam Call Blocker - Build Script for Codespaces
echo "🔨 Building Spam Call Blocker Android APK..."
echo "📁 Current directory: $(pwd)"

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found. Make sure you're in the spam-call-blocker directory"
    echo "💡 Try: cd spam-call-blocker"
    exit 1
fi

# Make gradlew executable
echo "🔧 Making gradlew executable..."
chmod +x gradlew

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📦 APK location: app/build/outputs/apk/debug/app-debug.apk"
    
    # Show APK info
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        echo "📊 APK size: $(du -h "$APK_PATH" | cut -f1)"
        echo ""
        echo "📱 To download:"
        echo "1. Navigate to app/build/outputs/apk/debug/ in file explorer"
        echo "2. Right-click app-debug.apk"
        echo "3. Select 'Download'"
        echo ""
        echo "🎉 Ready to install on your Android phone!"
    fi
else
    echo "❌ Build failed!"
    echo "📋 Check the error messages above"
    echo "💡 Try running: ./gradlew clean assembleDebug"
fi