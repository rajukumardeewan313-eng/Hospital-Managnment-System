#!/usr/bin/env powershell
# Hospital Management System - Run Script
# This script compiles and runs the HMS application

Write-Host ""
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Hospital Management System (HMS) v2.0" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ""

# Create lib folder if it doesn't exist
if (-not (Test-Path "lib")) {
    Write-Host "Creating lib folder..."
    New-Item -ItemType Directory -Path "lib" -Force | Out-Null
}

# Download SQLite driver if needed
if (-not (Test-Path "lib/sqlite-jdbc-3.44.0.0.jar")) {
    Write-Host "Downloading SQLite JDBC Driver..." -ForegroundColor Yellow
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri "https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar" -OutFile "lib/sqlite-jdbc-3.44.0.0.jar" -UseBasicParsing
        Write-Host "Driver downloaded successfully!" -ForegroundColor Green
    } catch {
        Write-Host "Warning: Could not download driver automatically" -ForegroundColor Yellow
    }
}

# Compile the application
Write-Host ""
Write-Host "[1/2] Compiling application..." -ForegroundColor Cyan
javac -d out -cp "lib/*" -sourcepath src src/App.java src/hms/database/*.java src/hms/model/*.java src/hms/service/*.java src/hms/ui/*.java src/hms/dao/*.java src/hms/billing/*.java src/hms/persistence/*.java src/hms/util/*.java 2>&1 | Where-Object {$_ -match "error"}

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation completed!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed. Check the errors above." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Run the application
Write-Host ""
Write-Host "[2/2] Starting application..." -ForegroundColor Cyan
Write-Host ""
java -cp "out;lib/*" App
