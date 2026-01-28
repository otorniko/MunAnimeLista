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
    @SerialName("alternative_titles") val alternativeTitles: AlternativeTitles? = null
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