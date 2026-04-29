import json
from http.server import BaseHTTPRequestHandler, HTTPServer
from pathlib import Path
from urllib.parse import unquote


ROOT = Path(__file__).resolve().parent
JSON_PATH = ROOT / "src" / "main" / "resources" / "media-1000.json"


def load_media():
    return json.loads(JSON_PATH.read_text(encoding="utf-8"))


def build_categories(media):
    categories = {}
    for item in media["data"]:
        category = item["category"]
        categories.setdefault(category, 0)
        categories[category] += 1

    return [
        {"name": name, "total": total}
        for name, total in sorted(categories.items(), key=lambda row: row[0].lower())
    ]


def media_items(media, media_type):
    return [item for item in media["data"] if item["mediaType"].lower() == media_type]


def build_artists(media):
    artists = []
    audio_items = media_items(media, "audio")
    artist_names = sorted({item["artist"] for item in audio_items if item.get("artist")})
    for index, name in enumerate(artist_names, start=1):
        songs = [item for item in audio_items if item.get("artist") == name]
        artists.append({
            "id": index,
            "name": name,
            "totalSongs": len(songs),
        })
    return artists


class MediaApiHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/":
            self.send_json({"message": "Media API is running"})
            return

        if self.path == "/api/health":
            self.send_json({"status": "ok"})
            return

        if self.path == "/api/media":
            self.send_raw_json(JSON_PATH.read_text(encoding="utf-8"))
            return

        if self.path == "/song":
            songs = media_items(load_media(), "audio")
            self.send_json({
                "success": True,
                "message": "Songs fetched successfully",
                "total": len(songs),
                "data": songs,
            })
            return

        if self.path in ("/song/artistg", "/song/artists"):
            artists = build_artists(load_media())
            self.send_json({
                "success": True,
                "message": "Artists fetched successfully",
                "total": len(artists),
                "data": artists,
            })
            return

        if self.path.startswith("/song/"):
            item_id_text = self.path.removeprefix("/song/")
            if item_id_text.isdigit():
                item_id = int(item_id_text)
                songs = media_items(load_media(), "audio")
                song = next((item for item in songs if item["id"] == item_id), None)
                if song is None:
                    self.send_json({"message": "Song not found"}, status=404)
                else:
                    self.send_json(song)
                return

        if self.path == "/videos":
            videos = media_items(load_media(), "video")
            self.send_json({
                "success": True,
                "message": "Videos fetched successfully",
                "total": len(videos),
                "data": videos,
            })
            return

        if self.path.startswith("/video/"):
            item_id_text = self.path.removeprefix("/video/")
            if item_id_text.isdigit():
                item_id = int(item_id_text)
                videos = media_items(load_media(), "video")
                video = next((item for item in videos if item["id"] == item_id), None)
                if video is None:
                    self.send_json({"message": "Video not found"}, status=404)
                else:
                    self.send_json(video)
                return

        if self.path.startswith("/artist/"):
            artist_id_text = self.path.removeprefix("/artist/")
            if artist_id_text.isdigit():
                artist_id = int(artist_id_text)
                media = load_media()
                artists = build_artists(media)
                artist = next((item for item in artists if item["id"] == artist_id), None)
                if artist is None:
                    self.send_json({"message": "Artist not found"}, status=404)
                else:
                    songs = [
                        item for item in media_items(media, "audio")
                        if item.get("artist") == artist["name"]
                    ]
                    self.send_json({
                        "success": True,
                        "message": "Artist fetched successfully",
                        "data": {
                            **artist,
                            "songs": songs,
                        },
                    })
                return

        if self.path == "/api/categories":
            media = load_media()
            categories = build_categories(media)
            self.send_json({
                "success": True,
                "message": "Categories fetched successfully",
                "total": len(categories),
                "data": categories,
            })
            return

        if self.path.startswith("/api/categories/"):
            category = unquote(self.path.removeprefix("/api/categories/")).strip()
            media = load_media()
            items = [
                item for item in media["data"]
                if item["category"].lower() == category.lower()
            ]
            if not items:
                self.send_json({"message": "Category not found"}, status=404)
            else:
                self.send_json({
                    "success": True,
                    "message": "Category media fetched successfully",
                    "category": items[0]["category"],
                    "total": len(items),
                    "data": items,
                })
            return

        if self.path.startswith("/api/media/"):
            item_id_text = self.path.removeprefix("/api/media/")
            if item_id_text.isdigit():
                item_id = int(item_id_text)
                media = load_media()
                item = next((row for row in media["data"] if row["id"] == item_id), None)
                if item is None:
                    self.send_json({"message": "Media item not found"}, status=404)
                else:
                    self.send_json(item)
                return

        self.send_json({"message": "Route not found"}, status=404)

    def do_POST(self):
        if self.path == "/api/media/echo":
            length = int(self.headers.get("Content-Length", "0"))
            body = self.rfile.read(length).decode("utf-8")
            self.send_raw_json(body)
            return

        self.send_json({"message": "Route not found"}, status=404)

    def send_json(self, body, status=200):
        self.send_raw_json(json.dumps(body, indent=2), status=status)

    def send_raw_json(self, body, status=200):
        encoded = body.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(encoded)))
        self.end_headers()
        self.wfile.write(encoded)


if __name__ == "__main__":
    server = HTTPServer(("localhost", 8080), MediaApiHandler)
    print("Media API running at http://localhost:8080")
    print("Press Ctrl+C to stop")
    server.serve_forever()
