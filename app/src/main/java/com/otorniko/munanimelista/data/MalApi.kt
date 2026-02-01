package com.otorniko.munanimelista.data

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MalApi {
    @GET("users/@me/animelist")
    suspend fun getUserAnimeList(
        @Query("fields") fields: String = "list_status,alternative_titles,main_picture,synopsis,mean,rank,popularity,media_type,genres,my_list_status,related_anime,recommendations",
        @Query("limit") limit: Int = 1000
    ): AnimeListResponse

    @GET("anime/{anime_id}")
    suspend fun getAnimeDetails(
        @Path("anime_id") animeId: Int,
        @Query("fields") fields: String =
            "id,title,main_picture,alternative_titles," +
                    "start_date,end_date,synopsis,mean,rank,popularity," +
                    "num_list_users,num_scoring_users,nsfw,created_at,updated_at," +
                    "media_type,status,genres,my_list_status,num_episodes," +
                    "start_season,broadcast,source,average_episode_duration," +
                    "rating,pictures,background," +
                    "related_anime{alternative_titles,media_type}," +
                    "recommendations{alternative_titles,media_type}," +
                    "studios,statistics"
    ): AnimeNode

    @FormUrlEncoded
    @PUT("anime/{anime_id}/my_list_status")
    suspend fun updateMyListStatus(
        @Path("anime_id") id: Int,
        @Field("status") status: String,
        @Field("score") score: Int? = null,
        @Field("num_watched_episodes") numWatchedEpisodes: Int? = null
    ): MyListStatus


    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,popularity,num_list_users,num_scoring_users,nsfw,created_at,updated_at,media_type,status,genres,my_list_status,num_episodes,start_season,broadcast,source,average_episode_duration,rating,pictures,background,studios,statistics"
    ): SearchResponse


    @FormUrlEncoded
    @POST("https://myanimelist.net/v1/oauth2/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String
    ): TokenResponse

    @GET("anime/ranking")
    suspend fun getTopAnime(
        @Query("ranking_type") type: String = "all",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String = "id,title,main_picture,mean,my_list_status,alternative_titles"
    ): SearchResponse

    @DELETE("anime/{anime_id}/my_list_status")
    suspend fun deleteMyListStatus(
        @Path("anime_id") animeId: Int
    )
}
