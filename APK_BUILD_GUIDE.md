# SafeCom APK Build & Distribution Guide

**Project:** SafeCom Task Management Android App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 15, 2025  
**Last Updated:** August 15, 2025 (Compilation fixes applied)

## 🚀 Quick Start - Build APK

### 📋 Prerequisites

#### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Ubuntu 18.04+
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: At least 5GB free space for Android build tools
- **Internet**: Stable connection for downloading dependencies

#### Required Software

1. **Java Development Kit (JDK)**
   - **Version Required**: JDK 11 or higher (JDK 17 recommended)
   - **Download**: [Eclipse Temurin JDK](https://adoptium.net/) (recommended) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - **Installation Guide**: 
     - Windows: Run the installer and add to PATH
     - macOS: Install using Homebrew `brew install openjdk@17`
     - Linux: `sudo apt install openjdk-17-jdk`
   - **Verification**: 
     ```bash
     java -version     # Should show Java 11+ 
     javac -version    # Should show Java compiler version
     echo $JAVA_HOME   # Should point to JDK installation
     ```

2. **Android SDK (Option A: Android Studio)**
   - **Version**: Android Studio Electric Eel (2022.1.1) or newer
   - **Download**: [Android Studio](https://developer.android.com/studio)
   - **Required Components**:
     - Android SDK Platform 33 (API Level 33) - minimum
     - Android SDK Platform 34 (API Level 34) - recommended  
     - Android SDK Build-Tools 33.0.0 or higher
     - Android SDK Platform-Tools
     - Android SDK Command-line Tools

3. **Android SDK (Option B: Command Line Tools Only)**
   - **Download**: [Android SDK Command Line Tools](https://developer.android.com/studio#command-tools)
   - **Setup**:
     ```bash
     # Create Android SDK directory
     mkdir ~/Android/Sdk  # Linux/macOS
     mkdir C:\Android\Sdk # Windows
     
     # Extract command line tools
     # Set ANDROID_HOME environment variable
     export ANDROID_HOME=~/Android/Sdk
     export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
     export PATH=$PATH:$ANDROID_HOME/platform-tools
     
     # Install required packages
     sdkmanager "platform-tools" "platforms;android-33" "platforms;android-34" "build-tools;33.0.0"
     ```

4. **Git Version Control**
   - **Download**: [Git SCM](https://git-scm.com/)
   - **Verification**: `git --version`

#### Environment Variables Setup

1. **Windows (PowerShell)**:
   ```powershell
   # Set JAVA_HOME
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.8.101-hotspot", [EnvironmentVariableTarget]::User)
   
   # Set ANDROID_HOME (if using command line tools)
   [Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Android\Sdk", [EnvironmentVariableTarget]::User)
   
   # Add to PATH
   $env:PATH += ";$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\cmdline-tools\latest\bin"
   ```

2. **macOS/Linux (bash/zsh)**:
   ```bash
   # Add to ~/.bashrc or ~/.zshrc
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk  # Linux
   export JAVA_HOME=/usr/local/opt/openjdk@17      # macOS
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin
   ```

### 🔧 Method 1: Build with Gradle Wrapper (Recommended)

#### Step 1: Clone and Navigate
```bash
# Clone repository (if not already done)
git clone https://github.com/Pushkarjay/SafeCom-App.git

# Navigate to Android project directory
cd SafeCom-App/android
```

#### Step 2: Verify Environment
```bash
# Check Java version
java -version

# Check if gradlew is executable
ls -la gradlew  # Linux/macOS
dir gradlew*    # Windows

# Make gradlew executable (if needed on Linux/macOS)
chmod +x gradlew
```

#### Step 3: Clean Previous Builds
```bash
# Clean any previous builds
./gradlew clean      # Linux/macOS
.\gradlew.bat clean  # Windows
```

#### Step 4: Build APK
```bash
# Debug build (for testing)
./gradlew assembleDebug      # Linux/macOS
.\gradlew.bat assembleDebug  # Windows

# Release build (for distribution)
./gradlew assembleRelease      # Linux/macOS
.\gradlew.bat assembleRelease  # Windows
```

### 🎯 Method 2: Build with Android Studio

#### Step 1: Import Project
1. **Open Android Studio**
2. **File** → **Open**
3. Navigate to `SafeCom-App/android` folder
4. Click **OK** to import project

#### Step 2: Sync Project
1. **Auto-sync**: Wait for automatic Gradle sync
2. **Manual sync**: Click **Sync Now** if prompted
3. **Troubleshooting**: If sync fails:
   - Check JDK version in **File** → **Project Structure** → **SDK Location**
   - Verify internet connection
   - Clear Gradle cache: **File** → **Invalidate and Restart**

#### Step 3: Build APK
1. **Build** → **Generate Signed Bundle / APK**
2. Select **APK** → **Next**
3. Choose build variant:
   - **debug**: For testing
   - **release**: For distribution (requires signing key)
4. Click **Finish**

### 📱 APK Output Locations

After successful build, APK files will be generated at:

#### Debug APK
```
SafeCom-App/android/app/build/outputs/apk/debug/
├── app-debug.apk                    # Main APK file
└── output-metadata.json            # Build metadata
```

#### Release APK
```
SafeCom-App/android/app/build/outputs/apk/release/
├── app-release-unsigned.apk         # Unsigned APK (needs signing)
├── app-release.apk                  # Signed APK (if keystore configured)
└── output-metadata.json            # Build metadata
```

### 🔐 APK Signing (For Release Distribution)

#### Generate Signing Key (First Time Only)
```bash
# Generate keystore file
keytool -genkey -v -keystore safecom-release-key.keystore -alias safecom -keyalg RSA -keysize 2048 -validity 10000

# Follow prompts to set passwords and information
```

#### Configure Signing in build.gradle
```gradle
// In android/app/build.gradle
android {
    signingConfigs {
        release {
            storeFile file('safecom-release-key.keystore')
            storePassword 'your-store-password'
            keyAlias 'safecom'
            keyPassword 'your-key-password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // other release configurations
        }
    }
}
```

### ✅ Verification & Testing

#### APK Installation
```bash
# Install on connected device/emulator
adb install app-debug.apk           # Debug version
adb install app-release.apk         # Release version

# Verify installation
adb shell pm list packages | grep safecom
```

#### APK Information
```bash
# Get APK information
aapt dump badging app-release.apk

# Check APK size
ls -lh app-release.apk              # Linux/macOS
dir app-release.apk                 # Windows
```

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
