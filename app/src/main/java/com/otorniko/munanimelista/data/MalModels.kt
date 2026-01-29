package com.otorniko.munanimelista.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- 1. The Wrapper Classes (Restoring these fixes the "Unresolved reference" error) ---
@Serializable
data class AnimeListResponse(
    val data: List<AnimeEdge>
)

@Serializable
data class AnimeEdge(
    val node: AnimeNode
)

// --- 2. The Main Data Class (With your new English Title support) ---
@Serializable
data class AnimeNode(
    val id: Int,
    val title: String, // Default Romaji title
    @SerialName("main_picture") val mainPicture: MainPicture? = null,
    @SerialName("alternative_titles") val alternativeTitles: AlternativeTitles? = null,
    val synopsis: String,
    val mean: Double,
    val rank: Int,
    val popularity: Int,
    @SerialName("media_type") val mediaType: MediaType,
    val genres: List<Genre>,
    @SerialName("my_list_status") val myListStatus: MyListStatus? = null,
    //val related_anime,
    //val recommendations

)

// --- 3. Helper Classes ---
@Serializable
data class AlternativeTitles(
    val en: String? = null,
    val ja: String? = null
)

@Serializable
data class MainPicture(
    val medium: String,
    val large: String
)
@Serializable
enum class ListStatus(val label: String) {
    @SerialName("watching") Watching("Currently Watching"),
    @SerialName("completed") Completed("Completed"),
    @SerialName("on_hold") OnHold("On Hold"),
    @SerialName("dropped") Dropped("Dropped"),
    @SerialName("plan_to_watch") PlanToWatch("Plan to Watch")
}

@Serializable
enum class MediaType {
    @SerialName("tv") TV,
    @SerialName("movie") MOVIE,
    @SerialName("special") SPECIAL,
    @SerialName("ova") OVA,
    @SerialName("ona") ONA,
    @SerialName("music") MUSIC,
    @SerialName("manga") MANGA
}

@Serializable
data class MyListStatus (
    val status: ListStatus,
    val score: Int,
    @SerialName("num_episodes_watched") val numEpisodesWatched: Int,
    @SerialName("is_rewatching") val isRewatching: Boolean,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class Genre (
    val id: Int,
    val name: String
)


