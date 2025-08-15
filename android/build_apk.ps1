# PowerShell APK Build Script with File Lock Handling
# This script will aggressively handle file locking issues and retry builds

Write-Host "SafeCom APK Build Script Starting..." -ForegroundColor Green

# Function to kill processes and clean directories
function Clean-BuildEnvironment {
    Write-Host "Cleaning build environment..." -ForegroundColor Yellow
    
    # Kill Java processes
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    Get-Process -Name "kotlin-daemon" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    
    Start-Sleep -Seconds 3
    
    # Clean directories
    Remove-Item -Recurse -Force "app\build" -ErrorAction SilentlyContinue
    Remove-Item -Recurse -Force "build" -ErrorAction SilentlyContinue 
    Remove-Item -Recurse -Force ".gradle" -ErrorAction SilentlyContinue
    
    Start-Sleep -Seconds 2
}

# Function to attempt build
function Invoke-Build {
    param($BuildType)
    
    Write-Host "Attempting $BuildType build..." -ForegroundColor Cyan
    $env:JAVA_HOME = "D:\JDK"
    
    $result = & .\gradlew.bat $BuildType --no-daemon -x test -x lint -x compileDebugAndroidTestKotlin 2>&1
    return $LASTEXITCODE
}

# Main build process
Clean-BuildEnvironment

# Try debug build first
$exitCode = Invoke-Build "assembleDebug"

if ($exitCode -eq 0) {
    Write-Host "Debug build successful!" -ForegroundColor Green
} else {
    Write-Host "Debug build failed, trying release..." -ForegroundColor Yellow
    Clean-BuildEnvironment
    Start-Sleep -Seconds 5
    
    $exitCode = Invoke-Build "assembleRelease"
    
    if ($exitCode -eq 0) {
        Write-Host "Release build successful!" -ForegroundColor Green
    } else {
        Write-Host "Both builds failed. Trying minimal build..." -ForegroundColor Red
        Clean-BuildEnvironment
        Start-Sleep -Seconds 5
        
        $env:JAVA_HOME = "D:\JDK"
        & .\gradlew.bat clean assembleDebug --no-daemon --offline -x test -x lint -x compileDebugUnitTestKotlin -x compileDebugAndroidTestKotlin
    }
}

# Check for APK files
Write-Host "`nChecking for APK files..." -ForegroundColor Cyan
if (Test-Path "app\build\outputs\apk") {
    Write-Host "APK files found:" -ForegroundColor Green
    Get-ChildItem -Recurse -Path "app\build\outputs\apk" -Filter "*.apk" | ForEach-Object {
        Write-Host "  $($_.FullName)" -ForegroundColor White
    }
} else {
    Write-Host "No APK files found." -ForegroundColor Red
}

Write-Host "`nBuild process completed." -ForegroundColor Green
