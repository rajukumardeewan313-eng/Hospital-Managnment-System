Set-StrictMode -Off
[Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12

$url = "https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar"
$output = "lib\sqlite-jdbc-3.44.0.0.jar"

Write-Host "Downloading SQLite JDBC Driver from GitHub..."
Write-Host "URL: $url"

try {
    Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing
    $file = Get-Item $output -ErrorAction Stop
    Write-Host "Download completed successfully!"
    Write-Host "File: $($file.FullName)"
    Write-Host "Size: $($file.Length) bytes"
} catch {
    Write-Host "Error downloading: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
