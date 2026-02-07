package com.otorniko.munanimelista.data.local

import androidx.room.TypeConverter
import com.otorniko.munanimelista.data.model.Genre
import com.otorniko.munanimelista.data.model.ListStatus
import com.otorniko.munanimelista.data.model.Recommendation
import com.otorniko.munanimelista.data.model.RelatedAnime
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStatus(status: ListStatus?): String? {
        return status?.apiValue
    }

    @TypeConverter
    fun toStatus(value: String?): ListStatus? {
        return ListStatus.entries.find { it.apiValue == value }
    }

    @TypeConverter
    fun fromGenres(genres: List<Genre>): String {
        return Json.encodeToString(genres) // Using Kotlinx.Serialization
    }

    @TypeConverter
    fun toGenres(data: String): List<Genre> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromRelatedAnime(relatedAnime: List<RelatedAnime>): String {
        return Json.encodeToString(relatedAnime)
    }

    @TypeConverter
    fun toRelatedAnime(data: String): List<RelatedAnime> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromRecommendations(recommendations: List<Recommendation>): String {
        return Json.encodeToString(recommendations)
    }

    @TypeConverter
    fun toRecommendations(data: String): List<Recommendation> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromStudios(studios: List<String>): String {
        return Json.encodeToString(studios)
    }

    @TypeConverter
    fun toStudios(data: String): List<String> {
        return Json.decodeFromString(data)
    }

}