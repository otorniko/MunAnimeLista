package com.otorniko.munanimelista.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.otorniko.munanimelista.data.model.AlternativeTitles
import com.otorniko.munanimelista.data.model.MyListStatus
import com.otorniko.munanimelista.data.model.Recommendation
import com.otorniko.munanimelista.data.model.RelatedAnime
import com.otorniko.munanimelista.data.model.StartSeason

@Entity(tableName = "anime_library")
data class AnimeEntity(
        @PrimaryKey val id: Int,
        val title: String,
        val mainPictureUrl: String?,
        @Embedded(prefix = "alternative_titles_") val alternativeTitles: AlternativeTitles?,
        val synopsis: String?,
        val meanScore: Double?,
        val rank: Int?,
        val popularity: Int?,
        val numEpisodes: Int,
        val mediaType: String?, // "tv", "movie", etc.
        val genres: List<String>,
        @Embedded(prefix = "myListStatus_") val myListStatus: MyListStatus?,
        val relatedAnime: List<RelatedAnime>,
        val recommendations: List<Recommendation>,
        val startDate: String?, // Format: "2022-10-10"
        val endDate: String?,
        @Embedded(prefix = "startSeason_") val startSeason: StartSeason?,
        val rating: String?, // e.g. "pg_13"
        val studios: List<String>,
        val status: String?, // "watching", "completed", etc.
        val source: String?, // e.g. "manga", "original"
                      )