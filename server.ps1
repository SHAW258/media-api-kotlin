$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$jsonPath = Join-Path $root "src\main\resources\media-1000.json"
$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add("http://localhost:8080/")
$listener.Start()

Write-Host "Media API running at http://localhost:8080"
Write-Host "Press Ctrl+C to stop"

function Send-Json {
    param(
        [System.Net.HttpListenerContext]$Context,
        [string]$Body,
        [int]$StatusCode = 200
    )

    $bytes = [System.Text.Encoding]::UTF8.GetBytes($Body)
    $Context.Response.StatusCode = $StatusCode
    $Context.Response.ContentType = "application/json; charset=utf-8"
    $Context.Response.ContentLength64 = $bytes.Length
    $Context.Response.OutputStream.Write($bytes, 0, $bytes.Length)
    $Context.Response.OutputStream.Close()
}

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $request = $context.Request
        $path = $request.Url.AbsolutePath.TrimEnd("/")
        if ($path -eq "") { $path = "/" }

        if ($request.HttpMethod -eq "GET" -and $path -eq "/") {
            Send-Json $context '{"message":"Media API is running"}'
            continue
        }

        if ($request.HttpMethod -eq "GET" -and $path -eq "/api/health") {
            Send-Json $context '{"status":"ok"}'
            continue
        }

        if ($request.HttpMethod -eq "GET" -and $path -eq "/api/media") {
            Send-Json $context (Get-Content -LiteralPath $jsonPath -Raw)
            continue
        }

        if ($request.HttpMethod -eq "GET" -and $path -match '^/api/media/(\d+)$') {
            $id = [int]$Matches[1]
            $media = Get-Content -LiteralPath $jsonPath -Raw | ConvertFrom-Json
            $item = $media.data | Where-Object { $_.id -eq $id } | Select-Object -First 1

            if ($null -eq $item) {
                Send-Json $context '{"message":"Media item not found"}' 404
            } else {
                Send-Json $context ($item | ConvertTo-Json -Depth 10)
            }
            continue
        }

        if ($request.HttpMethod -eq "POST" -and $path -eq "/api/media/echo") {
            $reader = [System.IO.StreamReader]::new($request.InputStream, $request.ContentEncoding)
            $body = $reader.ReadToEnd()
            $reader.Close()
            Send-Json $context $body
            continue
        }

        Send-Json $context '{"message":"Route not found"}' 404
    }
}
finally {
    $listener.Stop()
    $listener.Close()
}
