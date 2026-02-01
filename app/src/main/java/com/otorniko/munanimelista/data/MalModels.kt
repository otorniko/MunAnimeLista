@file:Suppress("unused")

package com.otorniko.munanimelista.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeListResponse(
    val data: List<AnimeEdge>
)

@Serializable
data class AnimeNode(
    val id: Int,
    val title: String, // Default Romaji title
    @SerialName("main_picture") val mainPicture: MainPicture? = null,
    @SerialName("alternative_titles") val alternativeTitles: AlternativeTitles? = null,
    val synopsis: String? = null,
    val mean: Double? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    @SerialName("num_episodes") val numEpisodes: Int? = null,
    @SerialName("media_type") val mediaType: MediaType? = null,
    val genres: List<Genre> = emptyList(),
    @SerialName("my_list_status") val myListStatus: MyListStatus? = null,
    @SerialName("related_anime") val relatedAnime: List<RelatedAnime> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
    @SerialName("start_date") val startDate: String? = null, // Format: "2022-10-10"
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("start_season") val startSeason: StartSeason? = null,
    val rating: Rating? = null, // e.g. "pg_13"
    val studios: List<Studio> = emptyList(),
    val status: String? = null, // e.g. "finished_airing", "currently_airing"
    val source: String? = null, // e.g. "manga", "original"
)

@Serializable
data class AnimeEdge(
    val node: AnimeNode,
    @SerialName("list_status") val listStatus: MyListStatus? = null
)

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
    @SerialName("watching")
    Watching("Currently Watching"),
    @SerialName("completed")
    Completed("Completed"),
    @SerialName("on_hold")
    OnHold("On Hold"),
    @SerialName("dropped")
    Dropped("Dropped"),
    @SerialName("plan_to_watch")
    PlanToWatch("Plan to Watch");

    val serialName: String
        get() = when (this) {
            Watching -> "watching"
            Completed -> "completed"
            OnHold -> "on_hold"
            Dropped -> "dropped"
            PlanToWatch -> "plan_to_watch"
        }
}

enum class MyListTab(val label: String, val status: ListStatus?) {
    ALL("All Anime", null),
    WATCHING("Currently Watching", ListStatus.Watching),
    COMPLETED("Completed", ListStatus.Completed),
    ON_HOLD("On Hold", ListStatus.OnHold),
    DROPPED("Dropped", ListStatus.Dropped),
    PLAN_TO_WATCH("Plan to Watch", ListStatus.PlanToWatch)
}

@Serializable
enum class MediaType {
    @SerialName("tv")
    TV,
    @SerialName("movie")
    MOVIE,
    @SerialName("special")
    SPECIAL,
    @SerialName("ova")
    OVA,
    @SerialName("ona")
    ONA,
    @SerialName("music")
    MUSIC,
    @SerialName("manga")
    MANGA,
    @SerialName("cm")
    CM,
    @SerialName("tv_special")
    TV_SPECIAL,
    @SerialName("pv")
    PV,
    @SerialName("unknown")
    UNKNOWN
}

@Serializable
data class MyListStatus(
    val status: ListStatus,
    val score: Int,
    @SerialName("num_episodes_watched") val numEpisodesWatched: Int,
    @SerialName("is_rewatching") val isRewatching: Boolean,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class RelatedAnime(
    val node: AnimeNode,
    @SerialName("relation_type") val relationType: String,
    @SerialName("relation_type_formatted") val relationTypeFormatted: String? = null
)

@Serializable
data class Recommendation(
    val node: AnimeNode,
    @SerialName("num_recommendations") val numRecommendations: Int
)

@Serializable
data class Studio(
    val id: Int,
    val name: String
)

@Serializable
data class StartSeason(
    val year: Int,
    val season: Season
)

enum class Season {
    @SerialName("winter")
    Winter,
    @SerialName("spring")
    Spring,
    @SerialName("summer")
    Summer,
    @SerialName("fall")
    Fall;

}

@Serializable
data class SearchResponse(
    val data: List<SearchNode>
)

@Serializable
data class SearchNode(
    val node: AnimeNode
)

@Serializable
data class TokenResponse(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

enum class RankingCategory(val label: String, val apiKey: String) {
    ALL("Top Anime Series", "all"),
    AIRING("Top Airing", "airing"),
    UPCOMING("Top Upcoming", "upcoming"),
    TV("Top TV Series", "tv"),
    OVA("Top OVA", "ova"),
    MOVIE("Top Movies", "movie"),
    SPECIAL("Top Specials", "special"),
    POPULARITY("By Popularity", "bypopularity"),
    FAVORITE("Most Favorited", "favorite");
}

@Serializable
enum class Rating(val label: String) {
    @SerialName("g")
    G("G - All Ages"),

    @SerialName("pg")
    PG("PG - Children"),

    @SerialName("pg_13")
    PG_13("PG-13 - Teens 13 or older"),

    @SerialName("r")
    R("R - 17+ (violence & profanity)"),

    @SerialName("r+")
    R_PLUS("R+ - Mild Nudity"),

    @SerialName("rx")
    RX("RX - Hentai");
}
