package com.otorniko.munanimelista.data

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.coroutines.cancellation.CancellationException

class AnimeViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private var _masterList = listOf<AnimeNode>()
    private val _currentFilter = MutableStateFlow<ListStatus?>(null)
    val currentFilter = _currentFilter.asStateFlow()
    private var currentSort: AnimeSortOrder = AnimeSortOrder.TITLE
    private val _animeList = MutableStateFlow<List<AnimeNode>>(emptyList())
    private val _searchResults = MutableStateFlow<List<AnimeNode>?>(null)
    val visibleList = combine(_animeList, _searchResults) { myAnime, searchResults ->
        searchResults ?: myAnime
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val json = Json { ignoreUnknownKeys = true }
    private val api by lazy { retrofit.create(MalApi::class.java) }
    private val _browseList = MutableStateFlow<List<AnimeNode>>(emptyList())
    val browseList = _browseList.asStateFlow()
    private var currentBrowseOffset = 0
    private var currentRankingType: String = "all"
    var isBrowseLoading by mutableStateOf(false)
    var currentSortType by mutableStateOf(SortType.LAST_UPDATED)
    var isSortAscending by mutableStateOf(false)

    private val _searchState = MutableStateFlow(TextFieldValue(""))
    val searchState = _searchState.asStateFlow()

    private val _isSearchBarVisible = MutableStateFlow(false)
    val isSearchBarVisible = _isSearchBarVisible.asStateFlow()

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
    fun initBrowse(rankingType: String) {
        if (currentRankingType == rankingType && _browseList.value.isNotEmpty()) return

        currentRankingType = rankingType
        _browseList.value = emptyList()
        currentBrowseOffset = 0
        loadMoreBrowse()
    }

    fun loadMoreBrowse() {
        if (isBrowseLoading) return
        isBrowseLoading = true

        viewModelScope.launch {
            try {
                val response = api.getTopAnime(
                    type = currentRankingType,
                    offset = currentBrowseOffset,
                    limit = 50
                )

                val newNodes = response.data.map { it.node }
                _browseList.value += newNodes
                currentBrowseOffset += 50

            } catch (e: Exception) {
                Log.e("Browse", "Failed to load", e)
            } finally {
                isBrowseLoading = false
            }
        }
    }

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val response = api.getUserAnimeList()
                val nodes = response.data.map { edge ->
                    edge.node.copy(myListStatus = edge.listStatus)
                }
                _masterList = nodes
                processList()
            } catch (e: Exception) {
                Log.e("AnimeViewModel", "Error fetching data", e)
            }
        }
    }

    private var searchJob: Job? = null
    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            try {
                val response = api.searchAnime(query = query)
                val nodes: List<AnimeNode> = response.data.map { it.node }
                _searchResults.value = nodes
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AnimeViewModel", "Search failed", e)
            }
        }
    }

    fun onSearchStateChange(newValue: TextFieldValue) {
        _searchState.value = newValue

        //        _searchState.value = newValue.copy(
//            selection = TextRange(newValue.text.length) // Always force cursor to end
//        )

        val queryText = newValue.text
        search(queryText)
    }

    fun clearSearch() {
        _searchState.value = TextFieldValue("")
        search("")
    }

    fun toggleSearchBar(isOpen: Boolean) {
        _isSearchBarVisible.value = isOpen
        if (!isOpen) {
            // Optional: Clear text when closing explicitly
            // _searchQuery.value = ""
        }
    }

    fun filterByStatus(status: ListStatus?) {
        _currentFilter.value = status
        processList()
    }

    fun changeSortOrder(order: AnimeSortOrder) {
        currentSort = order
        processList()
    }

    private fun processList() {
        val filter = _currentFilter.value
        var result = _masterList
        if (filter != null) {
            result = result.filter { it.myListStatus?.status == filter }
        }

        result = when (currentSortType) {
            SortType.TITLE -> {
                if (isSortAscending) result.sortedBy { it.title.lowercase() }
                else result.sortedByDescending { it.title.lowercase() }
            }
            SortType.SCORE -> {
                if (isSortAscending) result.sortedBy { it.mean ?: 0.0 }
                else result.sortedByDescending { it.mean ?: 0.0 }
            }
            SortType.LAST_UPDATED -> {
                if (isSortAscending) result.sortedBy { it.myListStatus?.updatedAt }
                else result.sortedByDescending { it.myListStatus?.updatedAt }
            }
        }

        _animeList.value = result
    }

    fun onSortClicked(newType: SortType) {
        if (currentSortType == newType) {
            isSortAscending = !isSortAscending
        } else {
            currentSortType = newType

            isSortAscending = (newType == SortType.TITLE)
        }
        processList()
    }

    private val settingsRepo = SettingsRepository(application)

    val preferEnglish = settingsRepo.preferEnglishTitles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

}

enum class AnimeSortOrder { TITLE, SCORE }
enum class SortType {
    TITLE, SCORE, LAST_UPDATED
}