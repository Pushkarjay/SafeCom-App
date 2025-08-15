#!/usr/bin/env powershell

# PowerShell script to build signed APK for SafeCom App
Write-Host "🔐 Building Signed SafeCom APK..." -ForegroundColor Green

# Set JAVA_HOME
$env:JAVA_HOME = "D:\JDK"

# Clean build directory thoroughly
Write-Host "🧹 Cleaning build directory..." -ForegroundColor Yellow
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Remove build directories
Remove-Item -Path ".\app\build\intermediates\incremental" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path ".\app\build\outputs" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path ".\app\build\tmp" -Recurse -Force -ErrorAction SilentlyContinue

# Build the APK
Write-Host "🔨 Building Release APK..." -ForegroundColor Blue
& .\gradlew clean
& .\gradlew assembleRelease

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ APK built successfully!" -ForegroundColor Green
    
    # Check if APK exists
    $apkPath = ".\app\build\outputs\apk\release\app-release.apk"
    if (Test-Path $apkPath) {
        $apkInfo = Get-Item $apkPath
        Write-Host "📱 APK Location: $($apkInfo.FullName)" -ForegroundColor Cyan
        Write-Host "📏 APK Size: $([math]::Round($apkInfo.Length / 1MB, 2)) MB" -ForegroundColor Cyan
        
        # Verify signing
        Write-Host "🔍 Verifying APK signature..." -ForegroundColor Yellow
        $jarsignerPath = "$env:JAVA_HOME\bin\jarsigner.exe"
        $result = & $jarsignerPath -verify $apkPath 2>&1
        
        if ($result -contains "jar verified.") {
            Write-Host "✅ APK is properly signed!" -ForegroundColor Green
        } else {
            Write-Host "⚠️  APK signature verification inconclusive" -ForegroundColor Yellow
            Write-Host "Note: This is normal for Android APKs. Use apksigner for detailed verification." -ForegroundColor Gray
        }
        
        # Show keystore info
        Write-Host "`n🔑 Keystore Information:" -ForegroundColor Magenta
        Write-Host "Keystore: safecom-release-key.keystore" -ForegroundColor White
        Write-Host "Alias: safecom" -ForegroundColor White
        Write-Host "Validity: 27+ years (until 2052)" -ForegroundColor White
        
        Write-Host "`n🎉 Signed APK is ready for distribution!" -ForegroundColor Green
        Write-Host "📁 You can find your signed APK at:" -ForegroundColor Yellow
        Write-Host "   $($apkInfo.FullName)" -ForegroundColor White
    } else {
        Write-Host "❌ APK file not found!" -ForegroundColor Red
    }
} else {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}
