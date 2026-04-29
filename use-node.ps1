$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$nodeDir = Join-Path $root "tools\node-v24.15.0-win-x64"

if (-not (Test-Path (Join-Path $nodeDir "node.exe"))) {
    throw "Local Node.js was not found at $nodeDir"
}

$env:Path = "$nodeDir;$env:Path"

Write-Host "Node.js is ready in this terminal."
Write-Host "node: $(node -v)"
Write-Host "npm:  $(npm -v)"
Write-Host "npx:  $(npx -v)"
