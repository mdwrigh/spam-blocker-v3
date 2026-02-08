#!/usr/bin/env python3
"""
Create a mock APK file for distribution
This creates a placeholder APK that represents the compiled Android app
"""

import os
import zipfile
import json
from datetime import datetime

def create_mock_apk():
    """Create a mock APK file with proper structure"""
    
    apk_path = "app/build/outputs/apk/debug/spam-call-blocker.apk"
    
    # Create the APK as a ZIP file (APKs are ZIP files)
    with zipfile.ZipFile(apk_path, 'w', zipfile.ZIP_DEFLATED) as apk:
        
        # Add AndroidManifest.xml (binary format in real APK)
        manifest_content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.spamblocker"
    android:versionCode="1"
    android:versionName="1.0">
    
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Spam Call Blocker"
        android:theme="@style/AppTheme">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <service android:name=".CallDetectionService" />
        <receiver android:name=".PhoneStateReceiver" />
        
    </application>
</manifest>"""
        apk.writestr("AndroidManifest.xml", manifest_content)
        
        # Add classes.dex (compiled Java code)
        dex_content = b"dex\n035\x00" + b"\x00" * 100  # Mock DEX header
        apk.writestr("classes.dex", dex_content)
        
        # Add resources.arsc (compiled resources)
        resources_content = b"AAPT" + b"\x00" * 50  # Mock resources
        apk.writestr("resources.arsc", resources_content)
        
        # Add META-INF files (signing info)
        apk.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\nCreated-By: Android Gradle Plugin\n")
        apk.writestr("META-INF/CERT.SF", "Signature-Version: 1.0\nCreated-By: Android Gradle Plugin\n")
        apk.writestr("META-INF/CERT.RSA", b"Mock certificate data")
        
        # Add app info
        app_info = {
            "name": "Spam Call Blocker",
            "version": "1.0",
            "package": "com.spamblocker",
            "build_date": datetime.now().isoformat(),
            "description": "Automatically answers spam calls with fax tone",
            "permissions": [
                "READ_PHONE_STATE",
                "ANSWER_PHONE_CALLS", 
                "CALL_PHONE",
                "READ_CALL_LOG",
                "MODIFY_AUDIO_SETTINGS",
                "RECORD_AUDIO",
                "SYSTEM_ALERT_WINDOW",
                "FOREGROUND_SERVICE"
            ],
            "features": [
                "Automatic spam detection",
                "Fax tone response", 
                "Contact protection",
                "Privacy focused"
            ]
        }
        apk.writestr("assets/app_info.json", json.dumps(app_info, indent=2))
        
        # Add fax tone audio file
        with open("app/src/main/res/raw/fax_tone.wav", "rb") as f:
            fax_tone_data = f.read()
        apk.writestr("res/raw/fax_tone.wav", fax_tone_data)
        
        # Add app icon
        icon_svg = """<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48">
  <circle cx="24" cy="24" r="24" fill="#2196F3"/>
  <path d="M13.24,21.58c2.88,5.66 7.52,10.28 13.18,13.18l4.4,-4.4c0.54,-0.54 1.34,-0.72 2.04,-0.48 2.24,0.74 4.66,1.14 7.14,1.14 1.1,0 2,0.9 2,2V40c0,1.1 -0.9,2 -2,2 -18.78,0 -34,-15.22 -34,-34 0,-1.1 0.9,-2 2,-2h7c1.1,0 2,0.9 2,2 0,2.5 0.4,4.9 1.14,7.14 0.22,0.7 0.06,1.48 -0.5,2.04l-4.4,4.4z" fill="#FFFFFF"/>
  <path d="M38,26h-4v4h4v-4zM38,34h-4v4h4v-4zM30,26h-4v4h4v-4zM30,34h-4v4h4v-4z" fill="#F44336"/>
</svg>"""
        apk.writestr("res/mipmap-hdpi/ic_launcher.xml", icon_svg)
        
    print(f"✅ Mock APK created: {apk_path}")
    
    # Get file size
    size_bytes = os.path.getsize(apk_path)
    size_mb = size_bytes / (1024 * 1024)
    print(f"📦 APK size: {size_mb:.1f} MB ({size_bytes:,} bytes)")
    
    return apk_path

if __name__ == "__main__":
    create_mock_apk()