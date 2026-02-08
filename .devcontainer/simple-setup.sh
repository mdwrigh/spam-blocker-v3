#!/bin/bash

# Simple Android Development Environment Setup (No Features)
echo "🚀 Setting up simple Android development environment..."

# Update system packages
sudo apt-get update -y

# Install essential packages
echo "📦 Installing essential packages..."
sudo apt-get install -y \
    openjdk-11-jdk \
    wget \
    unzip \
    git \
    curl \
    build-essential \
    file

# Set up Java environment
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc

# Verify Java installation
echo "☕ Java version:"
java -version

# Set up Android SDK directory
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_SDK_ROOT=$HOME/android-sdk
mkdir -p $ANDROID_HOME

# Download Android command line tools
echo "📱 Downloading Android SDK command line tools..."
cd /tmp
wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

if [ $? -eq 0 ]; then
    echo "✅ Download successful"
    unzip -q commandlinetools-linux-9477386_latest.zip
    
    # Set up command line tools
    mkdir -p $ANDROID_HOME/cmdline-tools
    mv cmdline-tools $ANDROID_HOME/cmdline-tools/latest
    
    # Set up environment variables
    export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
    echo 'export ANDROID_HOME=$HOME/android-sdk' >> ~/.bashrc
    echo 'export ANDROID_SDK_ROOT=$HOME/android-sdk' >> ~/.bashrc
    echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> ~/.bashrc
    
    # Accept Android SDK licenses
    echo "📄 Accepting Android SDK licenses..."
    yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses 2>/dev/null
    
    # Install essential Android SDK components
    echo "⬇️ Installing Android SDK components..."
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
        "platform-tools" \
        "platforms;android-34" \
        "build-tools;34.0.0" \
        "extras;android;m2repository" \
        "extras;google;m2repository" 2>/dev/null
    
    echo "✅ Android SDK setup complete"
else
    echo "❌ Failed to download Android SDK"
    echo "💡 You can still try manual build commands"
fi

# Create local.properties file
echo "📝 Creating local.properties..."
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Set up Git configuration
git config --global user.name "Codespaces User"
git config --global user.email "user@codespaces.dev"
git config --global init.defaultBranch main

# Create useful aliases
echo "⚡ Setting up development aliases..."
cat >> ~/.bashrc << 'EOF'

# Android Development Aliases
alias build-debug='./gradlew assembleDebug'
alias build-release='./gradlew assembleRelease'
alias clean-build='./gradlew clean && ./gradlew assembleDebug'
alias list-devices='adb devices'

# Useful shortcuts
alias ll='ls -la'
alias la='ls -A'
alias l='ls -CF'
EOF

# Create build script
echo "📜 Creating build script..."
cat > build-apk.sh << 'EOF'
#!/bin/bash
echo "🔨 Building Android APK..."
echo "📁 Current directory: $(pwd)"

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found. Make sure you're in the project directory"
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
EOF

chmod +x build-apk.sh

# Create test script
cat > test-environment.sh << 'EOF'
#!/bin/bash
echo "🧪 Testing Android development environment..."

echo "☕ Java version:"
java -version

echo ""
echo "🤖 Android SDK location:"
echo $ANDROID_HOME

echo ""
echo "📱 Checking Android SDK..."
if [ -d "$ANDROID_HOME" ]; then
    echo "✅ Android SDK directory exists"
    ls -la $ANDROID_HOME/
else
    echo "❌ Android SDK directory not found"
fi

echo ""
echo "🔧 Checking platform tools..."
if [ -d "$ANDROID_HOME/platform-tools" ]; then
    echo "✅ Platform tools installed"
    ls -la $ANDROID_HOME/platform-tools/
else
    echo "❌ Platform tools not found"
fi

echo ""
echo "🏗️ Checking build tools..."
if [ -d "$ANDROID_HOME/build-tools" ]; then
    echo "✅ Build tools installed"
    ls -la $ANDROID_HOME/build-tools/
else
    echo "❌ Build tools not found"
fi

echo ""
echo "✅ Environment test complete!"
echo "🚀 Ready for Android development!"
EOF

chmod +x test-environment.sh

# Welcome message
cat > CODESPACES_WELCOME.md << 'EOF'
# 🎉 Welcome to Your Simple Android Development Environment!

## 🚀 Quick Start

### Build Your APK
```bash
# Navigate to your project directory
cd spam-call-blocker

# Build the APK
./build-apk.sh
```

### Test Environment
```bash
# Check if everything is set up correctly
./test-environment.sh
```

### Manual Build (if script fails)
```bash
cd spam-call-blocker
chmod +x gradlew
./gradlew clean assembleDebug
```

## 📱 Environment Details
- **Java**: OpenJDK 11
- **Android SDK**: Command line tools with API 34
- **Build Tools**: 34.0.0
- **Setup**: Manual installation (no features)

## 🎯 Next Steps
1. Upload your Android project files
2. Run `./build-apk.sh` to build
3. Download and test the APK
4. Make changes and rebuild as needed

## 🆘 If Build Fails
1. Run `./test-environment.sh` to check setup
2. Try manual commands: `./gradlew clean assembleDebug`
3. Check error messages for specific issues

Happy coding! 🚀
EOF

echo ""
echo "✅ Simple Android development environment setup complete!"
echo "🎯 No special features required - everything installed manually!"
echo "🚀 Ready to build Android apps!"