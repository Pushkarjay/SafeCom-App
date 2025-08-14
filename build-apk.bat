@echo off
echo === SafeCom APK Quick Builder ===
echo Developer: Pushkarjay Ajay
echo Organization: SafeCom
echo.

cd /d "e:\SafeCom-App\android"

echo Checking Gradle wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading Gradle wrapper...
    mkdir gradle\wrapper 2>nul
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.0.2/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
)

echo Building SafeCom APK...
gradlew.bat assembleRelease

if exist "app\build\outputs\apk\release\app-release.apk" (
    echo.
    echo ✓ APK BUILD SUCCESSFUL!
    echo Location: %cd%\app\build\outputs\apk\release\app-release.apk
    
    set "date=%date:~-4%-%date:~3,2%-%date:~0,2%"
    mkdir "e:\SafeCom-Distribution\%date%" 2>nul
    copy "app\build\outputs\apk\release\app-release.apk" "e:\SafeCom-Distribution\%date%\SafeCom-TaskManager-v1.0.apk"
    
    echo.
    echo ✓ Distribution package created: e:\SafeCom-Distribution\%date%\
    echo Ready to share with clients!
) else (
    echo.
    echo ✗ Build failed. Check errors above.
)

pause
