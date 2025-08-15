@echo off
echo ===============================================
echo    SafeCom Signed APK Builder
echo    Developer: Pushkarjay Ajay
echo ===============================================
echo.

cd /d "%~dp0android"

echo [1/4] Setting up environment...
set JAVA_HOME=D:\JDK
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: JDK not found at %JAVA_HOME%
    echo Please ensure JDK is installed at D:\JDK
    pause
    exit /b 1
)

echo [2/4] Cleaning previous builds...
if exist "app\build" rmdir /s /q "app\build" 2>nul
if exist ".gradle" rmdir /s /q ".gradle" 2>nul

echo [3/4] Building signed release APK...
echo This may take several minutes...
echo.
call gradlew assembleRelease --no-daemon --quiet

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===============================================
    echo    BUILD SUCCESSFUL!
    echo ===============================================
    echo.
    if exist "app\build\outputs\apk\release\app-release.apk" (
        for %%i in ("app\build\outputs\apk\release\app-release.apk") do (
            echo APK Location: %%~fi
            echo APK Size: %%~zi bytes ^(~%~z0 bytes^)
        )
        echo.
        echo Ready for distribution!
        echo - Google Play Store upload
        echo - Direct installation
        echo - Production deployment
    ) else (
        echo WARNING: APK file not found at expected location
    )
) else (
    echo.
    echo ===============================================
    echo    BUILD FAILED!
    echo ===============================================
    echo Check the output above for error details
    echo.
    echo Common solutions:
    echo - Ensure JDK is properly installed
    echo - Check keystore file exists
    echo - Verify Android SDK is configured
    echo - Run 'gradlew clean' manually
)

echo.
echo [4/4] Cleaning temporary files...
if exist "build_signed_apk.ps1" del "build_signed_apk.ps1" 2>nul

echo.
echo Press any key to exit...
pause >nul
