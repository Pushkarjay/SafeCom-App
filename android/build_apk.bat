@echo off
echo Starting APK build process...

REM Kill any existing Java processes
taskkill /F /IM java.exe /T >nul 2>&1
taskkill /F /IM kotlin-daemon.exe /T >nul 2>&1
timeout /t 3 /nobreak >nul

REM Clean build directories
if exist "app\build" rmdir /s /q "app\build" >nul 2>&1
if exist "build" rmdir /s /q "build" >nul 2>&1
if exist ".gradle" rmdir /s /q ".gradle" >nul 2>&1

REM Set JAVA_HOME
set JAVA_HOME=D:\JDK

echo Attempting debug build...
call gradlew.bat assembleDebug --no-daemon -x test -x lint

if %ERRORLEVEL% neq 0 (
    echo Debug build failed, trying release build...
    timeout /t 2 /nobreak >nul
    call gradlew.bat assembleRelease --no-daemon -x test -x lint
)

if %ERRORLEVEL% neq 0 (
    echo Both builds failed, trying simplified build...
    timeout /t 2 /nobreak >nul
    call gradlew.bat clean assembleDebug --no-daemon --offline -x test -x lint -x compileDebugUnitTestKotlin
)

echo Build process completed.
if exist "app\build\outputs\apk" (
    echo APK files found:
    dir "app\build\outputs\apk" /s /b
) else (
    echo No APK files generated.
)

pause
