# SafeCom APK Build Setup Script
# Run this script to set up the build environment and create APK

Write-Host "=== SafeCom Task Management App - APK Builder ===" -ForegroundColor Green
Write-Host "Developer: Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)" -ForegroundColor Cyan
Write-Host "Organization: SafeCom" -ForegroundColor Cyan
Write-Host ""

# Check Java installation
Write-Host "Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✓ Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java not found. Please install JDK 11 or higher from https://adoptium.net/" -ForegroundColor Red
    exit 1
}

# Set working directory
$androidDir = "e:\SafeCom-App\android"
Set-Location $androidDir

Write-Host "Working directory: $androidDir" -ForegroundColor Cyan

# Download Gradle wrapper JAR if not exists
$wrapperJar = "gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $wrapperJar)) {
    Write-Host "Downloading Gradle wrapper JAR..." -ForegroundColor Yellow
    
    # Create wrapper directory if not exists
    New-Item -ItemType Directory -Path "gradle\wrapper" -Force | Out-Null
    
    # Download wrapper JAR
    $wrapperUrl = "https://github.com/gradle/gradle/raw/v8.0.2/gradle/wrapper/gradle-wrapper.jar"
    try {
        Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperJar
        Write-Host "✓ Gradle wrapper downloaded successfully" -ForegroundColor Green
    } catch {
        Write-Host "✗ Failed to download Gradle wrapper. Trying alternative method..." -ForegroundColor Red
        
        # Alternative: Use system gradle if available
        try {
            gradle --version | Out-Null
            Write-Host "✓ System Gradle found, will use gradle command instead" -ForegroundColor Green
            $useSystemGradle = $true
        } catch {
            Write-Host "✗ No Gradle found. Please install Android Studio or Gradle manually." -ForegroundColor Red
            Write-Host "Android Studio: https://developer.android.com/studio" -ForegroundColor Yellow
            exit 1
        }
    }
}

# Build the APK
Write-Host ""
Write-Host "Building SafeCom APK..." -ForegroundColor Yellow
Write-Host "This may take several minutes for the first build..." -ForegroundColor Gray

try {
    if ($useSystemGradle) {
        gradle assembleRelease
    } else {
        .\gradlew.bat assembleRelease
    }
    
    # Check if APK was created
    $apkPath = "app\build\outputs\apk\release\app-release.apk"
    if (Test-Path $apkPath) {
        $apkSize = (Get-Item $apkPath).length / 1MB
        Write-Host ""
        Write-Host "🎉 APK BUILD SUCCESSFUL!" -ForegroundColor Green
        Write-Host "📱 APK Location: $androidDir\$apkPath" -ForegroundColor Cyan
        Write-Host "📊 APK Size: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Cyan
        
        # Create distribution directory
        $distDate = Get-Date -Format "yyyy-MM-dd"
        $distDir = "e:\SafeCom-Distribution\$distDate"
        New-Item -ItemType Directory -Path $distDir -Force | Out-Null
        
        # Copy APK to distribution directory
        $finalApkName = "SafeCom-TaskManager-v1.0.apk"
        Copy-Item $apkPath "$distDir\$finalApkName"
        
        Write-Host ""
        Write-Host "📤 DISTRIBUTION PACKAGE CREATED:" -ForegroundColor Green
        Write-Host "   Location: $distDir" -ForegroundColor Cyan
        Write-Host "   APK File: $finalApkName" -ForegroundColor Cyan
        
        # Create installation instructions
        @"
SafeCom Task Management App - Installation Instructions

📱 APK File: $finalApkName
📅 Build Date: $distDate
👨‍💻 Developer: Pushkarjay Ajay
🏢 Organization: SafeCom
📧 Support: pushkarjay.ajay1@gmail.com

🔧 INSTALLATION STEPS:

For Android Devices:
1. Download the APK file to your Android device
2. Go to Settings > Security > Unknown Sources (Enable)
   OR Settings > Apps > Special Access > Install Unknown Apps
3. Open the downloaded APK file
4. Tap "Install" when prompted
5. Grant necessary permissions
6. Launch "SafeCom" from your app drawer

📋 SYSTEM REQUIREMENTS:
- Android 8.0 (API 26) or higher
- 50 MB free storage space
- Internet connection for sync features

🔐 PERMISSIONS REQUIRED:
- Internet access (for data sync)
- Camera (for document scanning)
- Storage (for file attachments)
- Notifications (for task alerts)

⚠️ SECURITY NOTE:
This APK is signed and safe to install. Only download from official SafeCom sources.

📞 SUPPORT:
For technical support or questions:
Email: pushkarjay.ajay1@gmail.com
Project: SafeCom Task Management System

---
Generated: $(Get-Date)
"@ | Out-File "$distDir\Installation-Instructions.txt" -Encoding UTF8

        Write-Host "   Instructions: Installation-Instructions.txt" -ForegroundColor Cyan
        
        Write-Host ""
        Write-Host "✅ READY FOR DISTRIBUTION!" -ForegroundColor Green
        Write-Host "Share the contents of: $distDir" -ForegroundColor Yellow
        
    } else {
        Write-Host "✗ APK not found. Build may have failed." -ForegroundColor Red
        Write-Host "Check the build output above for errors." -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "✗ Build failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please check the error messages above." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Build Process Complete ===" -ForegroundColor Green
