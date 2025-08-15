@echo off
echo ===============================================
echo    SafeCom Debug APK Builder (Crash Fixes)
echo    For troubleshooting app crashes
echo ===============================================
echo.

cd /d "%~dp0android"

echo [1/3] Setting up environment...
set JAVA_HOME=D:\JDK
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: JDK not found at %JAVA_HOME%
    echo Please ensure JDK is installed at D:\JDK
    pause
    exit /b 1
)

echo [2/3] Building debug APK with crash fixes...
echo This may take several minutes...
echo.
call gradlew assembleDebug --no-daemon

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===============================================
    echo    DEBUG APK BUILT SUCCESSFULLY!
    echo ===============================================
    echo.
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        for %%i in ("app\build\outputs\apk\debug\app-debug.apk") do (
            echo APK Location: %%~fi
            echo APK Size: %%~zi bytes
        )
        echo.
        echo INSTALLATION INSTRUCTIONS:
        echo 1. Uninstall previous version from phone
        echo 2. Install this debug APK
        echo 3. Test if app opens without crashing
        echo.
        echo CRASH FIXES APPLIED:
        echo - Firebase initialization error handling
        echo - Fixed deprecated Handler usage
        echo - Added Activity error handling
        echo - Proper Hilt dependency injection
    ) else (
        echo WARNING: APK file not found at expected location
    )
) else (
    echo.
    echo ===============================================
    echo    BUILD FAILED!
    echo ===============================================
    echo Check the output above for error details
)

echo.
echo [3/3] Done!
echo.
echo Press any key to exit...
pause >nul
