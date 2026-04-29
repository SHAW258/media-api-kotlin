# Media API Endpoint List

Use your deployed server URL as the base URL.

```text
BASE_URL=https://your-live-server-url
```

For example, if your deployed URL is:

```text
https://media-api-kotlin.onrender.com
```

then:

```text
GET https://media-api-kotlin.onrender.com/song
```

## Health

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/` | API running message |
| GET | `/api/health` | Health check |

Example:

```text
GET {BASE_URL}/api/health
```

## All Media

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/media` | Get all media items |
| GET | `/api/media/{id}` | Get one media item by ID |
| POST | `/api/media/echo` | Echo a JSON request body |

Examples:

```text
GET  {BASE_URL}/api/media
GET  {BASE_URL}/api/media/1
POST {BASE_URL}/api/media/echo
```

## Songs

Songs are audio items. In the sample data, song IDs are usually even numbers.

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/song` | Get all songs |
| GET | `/song/{id}` | Get one song by media ID |
| GET | `/song/artistg` | Get artist list |
| GET | `/song/artists` | Get artist list alias |

Examples:

```text
GET {BASE_URL}/song
GET {BASE_URL}/song/2
GET {BASE_URL}/song/artistg
GET {BASE_URL}/song/artists
```

## Artists

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/artist/{id}` | Get artist details and songs |

Artist IDs in the current sample data:

| ID | Artist |
| --- | --- |
| 1 | Demo Artist |
| 2 | Learning Container |
| 3 | SampleLib |
| 4 | SoundHelix |

Examples:

```text
GET {BASE_URL}/artist/1
GET {BASE_URL}/artist/2
GET {BASE_URL}/artist/3
GET {BASE_URL}/artist/4
```

## Videos

In the sample data, video IDs are usually odd numbers.

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/videos` | Get all videos |
| GET | `/video/{id}` | Get one video by media ID |

Examples:

```text
GET {BASE_URL}/videos
GET {BASE_URL}/video/1
GET {BASE_URL}/video/3
```

## Categories

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/categories` | Get all categories with totals |
| GET | `/api/categories/{category}` | Get media items in one category |

Current categories:

```text
Education
Entertainment
Instrumental
Music
Podcast
Relaxing
Streaming
Testing
```

Examples:

```text
GET {BASE_URL}/api/categories
GET {BASE_URL}/api/categories/Education
GET {BASE_URL}/api/categories/Entertainment
GET {BASE_URL}/api/categories/Instrumental
GET {BASE_URL}/api/categories/Music
GET {BASE_URL}/api/categories/Podcast
GET {BASE_URL}/api/categories/Relaxing
GET {BASE_URL}/api/categories/Streaming
GET {BASE_URL}/api/categories/Testing
```

## Quick Test List

Replace `{BASE_URL}` with your deployed URL:

```text
{BASE_URL}/api/health
{BASE_URL}/song
{BASE_URL}/song/2
{BASE_URL}/song/artistg
{BASE_URL}/artist/1
{BASE_URL}/videos
{BASE_URL}/video/1
{BASE_URL}/api/categories
{BASE_URL}/api/categories/Education
```
