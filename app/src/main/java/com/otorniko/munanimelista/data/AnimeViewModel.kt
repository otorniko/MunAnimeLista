package com.otorniko.munanimelista.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class AnimeViewModel : ViewModel() {

    // 1. STATE: Private mutable state, Public read-only state (Encapsulation)
    private val _animeList = MutableStateFlow<List<AnimeNode>>(emptyList())
    val animeList: StateFlow<List<AnimeNode>> = _animeList.asStateFlow()

    // 2. NETWORK SETUP (Singleton-like pattern inside the ViewModel)
    // We create the JSON parser once here to fix that warning you saw.
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.myanimelist.net/v2/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val api by lazy { retrofit.create(MalApi::class.java) }

    // 3. INIT: Fetch data immediately when ViewModel is created
    init {
        fetchAnime()
    }

    private fun fetchAnime() {
        // viewModelScope launches a coroutine that dies automatically if the app closes
        viewModelScope.launch {
            try {
                val response = api.getUserAnimeList()
                _animeList.value = response.data.map { it.node }
                Log.d("AnimeViewModel", "Success: Loaded ${response.data.size} items")
            } catch (e: Exception) {
                Log.e("AnimeViewModel", "Error fetching data", e)
            }
        }
    }
}