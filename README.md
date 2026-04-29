# Media API Kotlin

Kotlin/Ktor backend API created from `media-1000.json`.

## Run Kotlin API

This folder includes portable JDK/Gradle tools, so you can run the Kotlin API with:

```powershell
cd C:\Users\Indra\Desktop\MediaApiKotlin
.\run-kotlin.ps1
```

If you installed JDK 17 and Gradle globally, this also works:

```powershell
cd C:\Users\Indra\Desktop\MediaApiKotlin
gradle run
```

## Run Kotlin API With Cloudflare

Open PowerShell 1:

```powershell
cd C:\Users\Indra\Desktop\MediaApiKotlin
.\run-kotlin.ps1
```

Open PowerShell 2:

```powershell
cd C:\Users\Indra\Desktop\MediaApiKotlin
.\cloudflared-386.exe tunnel --url http://localhost:8080 --http-host-header localhost:8080 --protocol http2 --ha-connections 1
```

## Make It Live

Push this folder to GitHub, then deploy it as a Docker web service on Render or Railway.

### Render

1. Create a GitHub repository and push this project.
2. Go to Render > New > Web Service.
3. Connect your GitHub repo.
4. Set Language to `Docker`.
5. Deploy.

### Railway

1. Create a GitHub repository and push this project.
2. Go to Railway > New Project > Deploy from GitHub repo.
3. Select this repo.
4. Railway will detect the `Dockerfile` and deploy it.

Base URL:

```text
http://localhost:8080
```

## Endpoint List

See [ENDPOINTS.md](ENDPOINTS.md) for all available API routes and examples.

## Postman URLs

```text
GET  http://localhost:8080/api/health
GET  http://localhost:8080/api/media
GET  http://localhost:8080/api/media/1
POST http://localhost:8080/api/media/echo
```

For `POST /api/media/echo`, set Body > raw > JSON and paste any JSON.

## Optional Python Fallback

This project also includes `server.py`, a small Python API runner for testing immediately:

```powershell
cd C:\Users\Indra\Desktop\MediaApiKotlin
py server.py
```
