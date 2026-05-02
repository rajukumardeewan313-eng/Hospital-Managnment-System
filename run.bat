@echo off
REM Hospital Management System - Run Script
REM This script compiles and runs the HMS application

echo.
echo ===================================================
echo   Hospital Management System (HMS) v2.0
echo ===================================================
echo.

REM Check if lib folder exists
if not exist "lib" (
    echo Creating lib folder...
    mkdir lib
)

REM Check if SQLite driver exists, if not download it
if not exist "lib\sqlite-jdbc-3.44.0.0.jar" (
    echo.
    echo Downloading SQLite JDBC Driver...
    powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.ServicePointProtocol]::Tls12; Invoke-WebRequest -Uri 'https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar' -OutFile 'lib/sqlite-jdbc-3.44.0.0.jar' -UseBasicParsing" 2>nul
    if exist "lib\sqlite-jdbc-3.44.0.0.jar" (
        echo Driver downloaded successfully!
    ) else (
        echo Warning: Could not download driver automatically
    )
)

REM Compile the application
echo.
echo [1/2] Compiling application...
javac -d out -cp "lib/*" -sourcepath src src/App.java src/hms/database/*.java src/hms/model/*.java src/hms/service/*.java src/hms/ui/*.java src/hms/dao/*.java src/hms/billing/*.java src/hms/persistence/*.java src/hms/util/*.java 2>nul

if errorlevel 1 (
    echo Compilation failed. Check your Java installation.
    pause
    exit /b 1
)

echo Compilation completed!

REM Run the application
echo.
echo [2/2] Starting application...
echo.
java -cp "out;lib/*" App

pause
