package com.otorniko.munanimelista.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    // Insert or Update (if ID exists, replace it)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(anime: List<AnimeEntity>)

    // Clear everything (useful for a full refresh)
    @Query("DELETE FROM anime_library")
    suspend fun clearAll()

    // Get ALL anime (Flow updates UI automatically when DB changes!)
    @Query("SELECT * FROM anime_library")
    fun getAllAnime(): Flow<List<AnimeEntity>>

    // Get Filtered anime
    @Query("SELECT * FROM anime_library WHERE status = :status")
    fun getAnimeByStatus(status: String): Flow<List<AnimeEntity>>

    // Search
    @Query("SELECT * FROM anime_library WHERE title LIKE '%' || :query || '%'")
    fun searchAnime(query: String): Flow<List<AnimeEntity>>
}