package com.tuapp.mygame.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel(private val repository: SetupPreferencesRepo) : ViewModel() {
    private val _state = MutableStateFlow(SetupPreferences())
    val state: StateFlow<SetupPreferences> = _state.asStateFlow()

    val hasCompletedSetup: StateFlow<Boolean> = state
        .map { it.isComplete }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            repository.setupData.collect { preferences ->
                _state.value = preferences
            }
        }
    }

    fun updateAlias(name: String) {
        _state.update { it.copy(alias = name) }
    }

    fun updateGridSize(size: Int) {
        _state.update { it.copy(gridSize = size) }
    }

    fun updateTrackTime(track: Boolean) {
        _state.update { it.copy(trackTime = track) }
    }

    fun saveSetup() {
        val setup = _state.value
        viewModelScope.launch {
            repository.updateAlias(setup.alias)
            repository.updateGridSize(setup.gridSize)
            repository.updateTrackTime(setup.trackTime)
        }
    }
}
