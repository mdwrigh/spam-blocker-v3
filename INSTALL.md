# Installation Guide - Spam Call Blocker

## Quick Start

### Option 1: Using Android Studio (Recommended)
1. Download and install [Android Studio](https://developer.android.com/studio)
2. Download this project folder
3. Open Android Studio and select "Open an existing project"
4. Navigate to the `spam-call-blocker` folder and open it
5. Connect your Android device via USB (enable Developer Options and USB Debugging)
6. Click the green "Run" button or press Ctrl+R

### Option 2: Command Line Build
1. Install Android SDK and set ANDROID_HOME environment variable
2. Navigate to the project folder in terminal
3. Run: `./build.sh`
4. Install the APK: `adb install app/build/outputs/apk/debug/app-debug.apk`

## Device Setup

### Enable Developer Options
1. Go to Settings > About Phone
2. Tap "Build Number" 7 times
3. Go back to Settings > Developer Options
4. Enable "USB Debugging"

### Connect Device
1. Connect your Android device via USB
2. Allow USB debugging when prompted
3. Verify connection: `adb devices`

## First Run Setup

### 1. Grant Permissions
The app requires several permissions:
- **Phone**: To detect incoming calls
- **Audio**: To play fax tones
- **Overlay**: For call handling
- **Contacts**: To avoid blocking known numbers

### 2. Battery Optimization
To ensure the app works reliably:
1. Go to Settings > Battery > Battery Optimization
2. Find "Spam Call Blocker"
3. Select "Don't optimize"

### 3. Auto-start (Some devices)
On some Android devices:
1. Go to Settings > Apps > Auto-start
2. Enable auto-start for "Spam Call Blocker"

## Testing

### Test the App
1. Enable the spam blocker in the app
2. Call your phone from an unknown number
3. The app should automatically answer and play a fax tone
4. Check the notification to confirm it's working

### Troubleshooting
- **Not answering calls**: Check permissions and battery optimization
- **No fax tone**: Verify audio permissions and call volume
- **App stops working**: Add to protected apps list in your device settings

## Important Notes

⚠️ **Legal Compliance**: Check your local laws regarding call blocking and automated answering

⚠️ **Emergency Calls**: The app is designed to never block emergency services

⚠️ **Known Contacts**: Calls from your contact list will never be blocked

## Support

If you encounter issues:
1. Check that all permissions are granted
2. Verify the app is not being killed by battery optimization
3. Test with different phone numbers
4. Check the app logs in Android Studio for debugging

The app works best on Android 8.0+ but supports devices back to Android 6.0.