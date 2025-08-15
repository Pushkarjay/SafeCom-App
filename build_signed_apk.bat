@echo off
echo 🔐 Building Signed SafeCom APK...
echo.

cd /d %~dp0android

echo Setting JAVA_HOME...
set JAVA_HOME=D:\JDK

echo 🧹 Cleaning previous builds...
gradlew clean

echo 🔨 Building signed release APK...
gradlew assembleRelease --no-daemon

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ APK built successfully!
    echo 📱 Location: %~dp0android\app\build\outputs\apk\release\app-release.apk
    echo.
    dir "%~dp0android\app\build\outputs\apk\release\app-release.apk"
    echo.
    echo 🎉 Signed APK is ready for distribution!
) else (
    echo.
    echo ❌ Build failed! Check the output above for errors.
    pause
    exit /b 1
)

pause
