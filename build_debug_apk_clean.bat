@echo off
echo Building SafeCom App Debug APK...
cd /d "%~dp0android"

echo Stopping all Java processes...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 >nul

echo Cleaning build directory...
if exist "app\build" (
    rmdir /s /q "app\build" >nul 2>&1
)

echo Starting build...
gradlew.bat assembleDebug --no-daemon --info

if %errorlevel% equ 0 (
    echo.
    echo ================================
    echo BUILD SUCCESSFUL!
    echo ================================
    echo APK Location: android\app\build\outputs\apk\debug\app-debug.apk
    echo.
    pause
) else (
    echo.
    echo ================================
    echo BUILD FAILED!
    echo ================================
    echo Check the output above for errors.
    echo.
    pause
)
