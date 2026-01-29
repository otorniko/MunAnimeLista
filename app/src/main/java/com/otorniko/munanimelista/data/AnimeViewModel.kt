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

    // 1. MASTER DATA (The Truth)
    private var _masterList = listOf<AnimeNode>()

    // 2. SETTINGS (The Rules)
    private var currentFilter: ListStatus? = null
    private var currentSort: AnimeSortOrder = AnimeSortOrder.TITLE

    // 3. UI STATE (The Result)
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
        viewModelScope.launch {
            try {
                val response = api.getUserAnimeList()
                val nodes = response.data.map { it.node }

                // Save to Master
                _masterList = nodes

                // Run the pipeline for the first time
                processList()

            } catch (e: Exception) {
                Log.e("AnimeViewModel", "Error fetching data", e)
            }
        }
    }

    fun filterByStatus(status: ListStatus?) {
        currentFilter = status
        processList() // Don't calculate here, just ask the pipeline to run
    }

    fun changeSortOrder(order: AnimeSortOrder) {
        currentSort = order
        processList()
    }

    // --- THE PIPELINE (The Brains) ---
    // This function combines all rules to produce the final list
    private fun processList() {
        var result = _masterList

        // Step 1: Apply Filter (if any)
        if (currentFilter != null) {
            result = result.filter { it.myListStatus?.status == currentFilter }
        }

        // Step 2: Apply Sort
        result = when (currentSort) {
            AnimeSortOrder.TITLE -> result.sortedBy { it.title }
            AnimeSortOrder.SCORE -> result.sortedByDescending { it.mean }
        }

        // Step 3: Publish to UI
        _animeList.value = result
    }
}

// Helper Enum for Sort (if you don't have it yet)
enum class AnimeSortOrder { TITLE, SCORE }
