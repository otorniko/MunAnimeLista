package com.otorniko.munanimelista.data

import retrofit2.http.GET
import retrofit2.http.Query

interface MalApi {
    @GET("users/@me/animelist")
    suspend fun getUserAnimeList(
        // We request the 'alternative_titles' field here
        @Query("fields") fields: String = "list_status,alternative_titles,main_picture",
        @Query("limit") limit: Int = 100
    ): AnimeListResponse
}