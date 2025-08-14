# SafeCom APK Build & Distribution Guide

**Project:** SafeCom Task Management Android App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 15, 2025

## 🚀 Quick Start - Build APK

### Prerequisites
1. **Java Development Kit (JDK) 11 or higher**
   ```powershell
   # Check if Java is installed
   java -version
   ```
   If not installed, download from: https://adoptium.net/

2. **Android SDK** (if building locally)
   - Download Android Studio: https://developer.android.com/studio
   - Or install SDK tools only

### Method 1: Build with Gradle Wrapper (Recommended)

Open PowerShell as Administrator and run:

```powershell
# Navigate to the Android project directory
cd "e:\SafeCom-App\android"

# Make gradlew executable (if needed)
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Build release APK
.\gradlew.bat assembleRelease

# The APK will be generated at:
# android\app\build\outputs\apk\release\app-release.apk
```

### Method 2: Build with Android Studio

1. Open Android Studio
2. Open project: `e:\SafeCom-App\android`
3. Go to **Build** → **Generate Signed Bundle / APK**
4. Select **APK** → **Next**
5. Choose **release** build variant
6. Click **Finish**

## 📱 APK Location

After successful build, find your APK at:
```
e:\SafeCom-App\android\app\build\outputs\apk\release\app-release.apk
```

## 🔒 Signing the APK (Production)

For production distribution, you need to sign the APK:

### Generate Keystore (One-time setup)
```powershell
# Create keystore
keytool -genkey -v -keystore safecom-release-key.keystore -alias safecom -keyalg RSA -keysize 2048 -validity 10000

# Move keystore to secure location
Move-Item safecom-release-key.keystore "C:\SafeCom\keys\"
```

### Sign APK
```powershell
# Sign the APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore "C:\SafeCom\keys\safecom-release-key.keystore" "e:\SafeCom-App\android\app\build\outputs\apk\release\app-release.apk" safecom

# Optimize APK
zipalign -v 4 "e:\SafeCom-App\android\app\build\outputs\apk\release\app-release.apk" "e:\SafeCom-App\android\app\build\outputs\apk\release\SafeCom-v1.0-signed.apk"
```

## 📤 Distribution Methods

### For Client & Employees

#### Option 1: Direct File Sharing
- **Email**: Attach APK (if under 25MB)
- **Google Drive**: Upload and share link
- **OneDrive**: Upload and share link
- **WeTransfer**: For temporary sharing

#### Option 2: Professional Distribution
- **Firebase App Distribution**: Free, professional testing
- **Google Play Console**: Internal testing track
- **Enterprise MDM**: For corporate deployment

### For End Customers

#### Option 1: Google Play Store (Recommended)
1. Create Google Play Console account ($25 one-time fee)
2. Upload signed APK/AAB
3. Complete store listing
4. Submit for review

#### Option 2: Direct APK Distribution
```powershell
# Create distribution package
$date = Get-Date -Format "yyyy-MM-dd"
New-Item -ItemType Directory -Path "e:\SafeCom-Distribution\$date" -Force

# Copy APK with version info
Copy-Item "e:\SafeCom-App\android\app\build\outputs\apk\release\app-release.apk" "e:\SafeCom-Distribution\$date\SafeCom-TaskManager-v1.0.apk"

# Create installation guide
@"
SafeCom Task Management App - Installation Guide

1. Enable 'Unknown Sources' in Android Settings
2. Download SafeCom-TaskManager-v1.0.apk
3. Tap the downloaded file to install
4. Grant necessary permissions
5. Launch SafeCom from app drawer

Support: pushkarjay.ajay1@gmail.com
"@ | Out-File "e:\SafeCom-Distribution\$date\Installation-Guide.txt"
```

## 🔧 Build Troubleshooting

### Common Issues

#### "Java not found"
```powershell
# Set JAVA_HOME environment variable
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-11.0.19.7-hotspot", "User")
```

#### "Android SDK not found"
```powershell
# Set ANDROID_HOME environment variable
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk", "User")
```

#### "Gradle build failed"
```powershell
# Clean and rebuild
.\gradlew.bat clean
.\gradlew.bat assembleRelease
```

## 📋 Build Variants

### Debug APK (for testing)
```powershell
.\gradlew.bat assembleDebug
# Output: android\app\build\outputs\apk\debug\app-debug.apk
```

### Release APK (for distribution)
```powershell
.\gradlew.bat assembleRelease
# Output: android\app\build\outputs\apk\release\app-release.apk
```

## 📊 APK Information

- **Package Name**: `com.safecom.taskmanagement`
- **Version Code**: 1
- **Version Name**: 1.0
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Size**: ~15-25 MB (estimated)

## 🛡️ Security Considerations

1. **Keep keystore secure** - Never commit to version control
2. **Use signed APKs** for production
3. **Enable ProGuard** for code obfuscation
4. **Test thoroughly** before distribution

## 📞 Support

**Developer**: Pushkarjay Ajay  
**Email**: pushkarjay.ajay1@gmail.com  
**Organization**: SafeCom  
**Project Repository**: https://github.com/Pushkarjay/SafeCom-App

---

*Generated on August 15, 2025*
