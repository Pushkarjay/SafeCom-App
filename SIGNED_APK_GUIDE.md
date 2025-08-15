# 🔐 Signed APK Build Guide for SafeCom App

This guide explains how to build a signed APK for production distribution of the SafeCom Task Management app.

## 📋 Prerequisites

Before building a signed APK, ensure you have:

- ✅ JDK 11+ installed (located at `D:\JDK` on your system)
- ✅ Android SDK Platform 33/34
- ✅ Android Build Tools
- ✅ Gradle 8.4+
- ✅ All project dependencies resolved

## 🔑 Keystore Information

**Keystore Details:**
- **File**: `safecom-release-key.keystore`
- **Location**: `E:\SafeCom-App\android\safecom-release-key.keystore`
- **Alias**: `safecom`
- **Password**: `Pushkar06`
- **Algorithm**: RSA 2048-bit
- **Validity**: 27+ years (until Dec 31, 2052)
- **Certificate**: CN=SafeCom App, OU=Development, O=SafeCom

## 🏗️ Building Signed APK

### Method 1: Using Gradle (Recommended)

```powershell
# Navigate to Android project directory
cd E:\SafeCom-App\android

# Set JAVA_HOME environment variable
$env:JAVA_HOME = "D:\JDK"

# Clean previous builds (if needed)
.\gradlew clean

# Build signed release APK
.\gradlew assembleRelease --no-daemon
```

### Method 2: Using Custom Build Script

```powershell
# Run the custom PowerShell build script
.\build_signed_apk.ps1
```

## 📱 Output Locations

After successful build, you'll find:

- **Signed APK**: `app\build\outputs\apk\release\app-release.apk`
- **App Bundle**: `app\build\outputs\bundle\release\app-release.aab` (if built)
- **Build Metadata**: `app\build\outputs\apk\release\output-metadata.json`

## ✅ Verification

### Check APK Details
```powershell
# View APK file information
Get-Item ".\app\build\outputs\apk\release\app-release.apk"

# Check keystore details
$env:JAVA_HOME = "D:\JDK"
$keytoolPath = "$env:JAVA_HOME\bin\keytool.exe"
& $keytoolPath -list -v -keystore safecom-release-key.keystore -storepass "Pushkar06"
```

### APK Information
- **Size**: ~14.36 MB
- **Target SDK**: 34
- **Minimum SDK**: 26
- **Version Code**: 1
- **Version Name**: 1.0

## 🔒 Security Notes

### Keystore Security
- **NEVER** commit the keystore file to version control
- **ALWAYS** backup the keystore securely
- **REMEMBER** the keystore password - it cannot be recovered
- **STORE** keystore in a secure location with restricted access

### Production Considerations
1. **Keystore Backup**: Create multiple secure backups of your keystore
2. **Password Management**: Use a secure password manager
3. **Access Control**: Limit keystore access to authorized personnel only
4. **Version Control**: Add `*.keystore` to `.gitignore`

## 🚀 Distribution

### Google Play Store
- Upload the signed APK or AAB to Google Play Console
- The APK is ready for internal testing, alpha, beta, or production release

### Direct Distribution
- The signed APK can be distributed directly to users
- Users may need to enable "Install from unknown sources"

## 🛠️ Troubleshooting

### Common Issues

1. **File Lock Error**
   ```
   Solution: Stop Java processes and clean build directory
   Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
   Remove-Item -Path ".\app\build" -Recurse -Force
   ```

2. **Keystore Not Found**
   ```
   Solution: Verify keystore path in build.gradle
   storeFile file('../safecom-release-key.keystore')
   ```

3. **Signing Config Error**
   ```
   Solution: Check signing configuration in app/build.gradle
   Ensure all passwords and alias are correct
   ```

## 📁 Project Structure

```
android/
├── safecom-release-key.keystore    # Signing keystore
├── build_signed_apk.ps1           # Custom build script
├── app/
│   ├── build.gradle               # App configuration with signing
│   └── build/outputs/apk/release/ # Generated APKs
└── gradle/                        # Gradle wrapper
```

## 🔄 Automated Build

For CI/CD pipelines, consider:
- Securely storing keystore and passwords
- Using environment variables for sensitive data
- Implementing automated signing verification
- Setting up artifact storage for signed APKs

## 📞 Support

If you encounter issues:
1. Check this troubleshooting guide
2. Verify all prerequisites are met
3. Ensure keystore file exists and is accessible
4. Check Gradle and Java versions

---

**Last Updated**: August 15, 2025  
**APK Version**: 1.0  
**Build Status**: ✅ Successfully Built and Signed
