package com.otorniko.munanimelista.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    val preferEnglish: StateFlow<Boolean> = repository.preferEnglishTitles
            .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = true
                    )

    fun toggleEnglishPreference(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPreferEnglish(enabled)
        }
    }
}