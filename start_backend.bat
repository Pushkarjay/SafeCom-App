@echo off
echo ===============================================
echo    SafeCom Backend Server Starter
echo    For SafeCom Task Management App
echo ===============================================
echo.

cd /d "%~dp0backend"

echo [1/4] Checking Node.js...
node --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Node.js not found!
    echo Please install Node.js 16+ from: https://nodejs.org/
    pause
    exit /b 1
)

echo [2/4] Checking dependencies...
if not exist "node_modules" (
    echo Installing dependencies...
    npm install
)

echo [3/4] Checking environment configuration...
if not exist ".env" (
    echo ERROR: .env file not found!
    echo Creating basic .env from .env.example...
    copy ".env.example" ".env"
)

echo [4/4] Starting backend server...
echo.
echo ===============================================
echo    BACKEND SERVER STARTING...
echo    URL: http://localhost:3000
echo    API: http://localhost:3000/api
echo ===============================================
echo.
echo Press Ctrl+C to stop the server
echo.

npm run dev
