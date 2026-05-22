package com.tuapp.mygame.features.setup

import kotlinx.coroutines.flow.Flow

interface SetupPreferencesInterface {
    val setupData: Flow<SetupPreferences>
    suspend fun updateAlias(name: String)
    suspend fun updateGridSize(size: Int)
    suspend fun updateTrackTime(track: Boolean)
    suspend fun clearData()
}