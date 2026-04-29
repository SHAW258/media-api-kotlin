$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$jdkHome = Get-ChildItem -LiteralPath (Join-Path $root "tools") -Directory -Filter "jdk-*" |
    Select-Object -First 1 -ExpandProperty FullName
$gradleHome = Join-Path $root "tools\gradle-8.10.2"

if (-not $jdkHome) {
    throw "JDK not found in tools\. Download/extract JDK 17 first."
}

if (-not (Test-Path (Join-Path $gradleHome "bin\gradle.bat"))) {
    throw "Gradle not found at $gradleHome."
}

$env:JAVA_HOME = $jdkHome
$env:PATH = "$jdkHome\bin;$gradleHome\bin;$env:PATH"

Set-Location $root
gradle run --no-daemon
