package com.otorniko.munanimelista.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class AnimeDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val json = Json { ignoreUnknownKeys = true }
    private val _animeDetails = MutableStateFlow<AnimeNode?>(null)
    val animeDetails = _animeDetails.asStateFlow()
    private val settingsRepo = SettingsRepository(application)
    val preferEnglish = settingsRepo.preferEnglishTitles
            .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = true
                    )
    private val retrofit by lazy {
        val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

        Retrofit.Builder()
                .baseUrl("https://api.myanimelist.net/v2/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
    }
    private val api by lazy { retrofit.create(MalApi::class.java) }

    fun loadAnime(id: Int) {
        viewModelScope.launch {
            try {
                val node = api.getAnimeDetails(id)
                _animeDetails.value = node
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error loading details", e)
            }
        }
    }

    fun updateStatus(status: ListStatus, score: Int, progress: Int) {
        viewModelScope.launch {
            try {
                val currentAnime = _animeDetails.value ?: return@launch
                val newStatus = api.updateMyListStatus(
                        id = currentAnime.id,
                        status = status.name.lowercase(), //here
                        score = score,
                        numWatchedEpisodes = progress
                                                      )
                _animeDetails.value = currentAnime.copy(
                        myListStatus = newStatus
                                                       )

            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Failed to update status", e)
            }
        }
    }

    fun removeAnime(animeId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                api.deleteMyListStatus(animeId)
                val current = _animeDetails.value
                if (current != null) {
                    _animeDetails.value = current.copy(myListStatus = null)
                }

                onSuccess()

            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Failed to delete anime", e)
            }
        }
    }
}