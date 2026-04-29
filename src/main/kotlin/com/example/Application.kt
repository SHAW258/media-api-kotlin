package com.example

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

private val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

@Serializable
data class MediaResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data: List<MediaItem>
)

@Serializable
data class MediaItem(
    val id: Int,
    val title: String,
    val description: String,
    val mediaType: String,
    val format: String,
    val category: String,
    val duration: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val artist: String? = null,
    val views: Int,
    val likes: Int,
    @SerialName("isPremium")
    val isPremium: Boolean,
    val createdAt: String
)

@Serializable
data class CategorySummary(
    val name: String,
    val total: Int
)

@Serializable
data class CategoriesResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data: List<CategorySummary>
)

@Serializable
data class CategoryMediaResponse(
    val success: Boolean,
    val message: String,
    val category: String,
    val total: Int,
    val data: List<MediaItem>
)

@Serializable
data class MediaListResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data: List<MediaItem>
)

@Serializable
data class ArtistSummary(
    val id: Int,
    val name: String,
    val totalSongs: Int
)

@Serializable
data class ArtistsResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data: List<ArtistSummary>
)

@Serializable
data class ArtistDetail(
    val id: Int,
    val name: String,
    val totalSongs: Int,
    val songs: List<MediaItem>
)

@Serializable
data class ArtistDetailResponse(
    val success: Boolean,
    val message: String,
    val data: ArtistDetail
)

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(json)
        }

        val mediaJsonText = loadMediaJson()
        val mediaResponse = json.decodeFromString<MediaResponse>(mediaJsonText)
        val songs = mediaResponse.data.filter { it.mediaType.equals("audio", ignoreCase = true) }
        val videos = mediaResponse.data.filter { it.mediaType.equals("video", ignoreCase = true) }
        val artists = songs
            .mapNotNull { it.artist }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .mapIndexed { index, name ->
                ArtistSummary(
                    id = index + 1,
                    name = name,
                    totalSongs = songs.count { it.artist == name }
                )
            }

        routing {
            get("/") {
                call.respond(mapOf("message" to "Media API is running"))
            }

            get("/api/health") {
                call.respond(mapOf("status" to "ok"))
            }

            get("/api/media") {
                call.respondText(mediaJsonText, ContentType.Application.Json)
            }

            get("/song") {
                call.respond(
                    MediaListResponse(
                        success = true,
                        message = "Songs fetched successfully",
                        total = songs.size,
                        data = songs
                    )
                )
            }

            get("/song/artistg") {
                call.respond(
                    ArtistsResponse(
                        success = true,
                        message = "Artists fetched successfully",
                        total = artists.size,
                        data = artists
                    )
                )
            }

            get("/song/artists") {
                call.respond(
                    ArtistsResponse(
                        success = true,
                        message = "Artists fetched successfully",
                        total = artists.size,
                        data = artists
                    )
                )
            }

            get("/song/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val song = songs.firstOrNull { it.id == id }

                if (song == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Song not found"))
                } else {
                    call.respond(song)
                }
            }

            get("/artist/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val artist = artists.firstOrNull { it.id == id }

                if (artist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Artist not found"))
                } else {
                    val artistSongs = songs.filter { it.artist == artist.name }
                    call.respond(
                        ArtistDetailResponse(
                            success = true,
                            message = "Artist fetched successfully",
                            data = ArtistDetail(
                                id = artist.id,
                                name = artist.name,
                                totalSongs = artist.totalSongs,
                                songs = artistSongs
                            )
                        )
                    )
                }
            }

            get("/videos") {
                call.respond(
                    MediaListResponse(
                        success = true,
                        message = "Videos fetched successfully",
                        total = videos.size,
                        data = videos
                    )
                )
            }

            get("/video/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val video = videos.firstOrNull { it.id == id }

                if (video == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Video not found"))
                } else {
                    call.respond(video)
                }
            }

            get("/api/categories") {
                val categories = mediaResponse.data
                    .groupingBy { it.category }
                    .eachCount()
                    .toSortedMap(String.CASE_INSENSITIVE_ORDER)
                    .map { (name, total) -> CategorySummary(name, total) }

                call.respond(
                    CategoriesResponse(
                        success = true,
                        message = "Categories fetched successfully",
                        total = categories.size,
                        data = categories
                    )
                )
            }

            get("/api/categories/{category}") {
                val category = call.parameters["category"]
                    ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
                    ?.trim()
                    .orEmpty()
                val items = mediaResponse.data.filter { it.category.equals(category, ignoreCase = true) }

                if (items.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Category not found"))
                } else {
                    call.respond(
                        CategoryMediaResponse(
                            success = true,
                            message = "Category media fetched successfully",
                            category = items.first().category,
                            total = items.size,
                            data = items
                        )
                    )
                }
            }

            get("/api/media/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val item = mediaResponse.data.firstOrNull { it.id == id }

                if (item == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Media item not found"))
                } else {
                    call.respond(item)
                }
            }

            post("/api/media/echo") {
                val body = call.receiveText()
                call.respondText(body, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}

private fun loadMediaJson(): String {
    val resource = Thread.currentThread().contextClassLoader.getResource("media-1000.json")
    if (resource != null) {
        return File(resource.toURI()).readText()
    }

    return File("src/main/resources/media-1000.json").readText()
}
