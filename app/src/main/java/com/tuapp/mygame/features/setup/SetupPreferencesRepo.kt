package com.tuapp.mygame.features.setup

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SetupPreferencesRepo(
    private val dataStore: DataStore<Preferences>
) : SetupPreferencesInterface {

    private object PreferenceKeys {
        val ALIAS = stringPreferencesKey("alias")
        val GRID_SIZE = intPreferencesKey("grid_size")
        val TRACK_TIME = booleanPreferencesKey("track_time")
    }

    override val setupData: Flow<SetupPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            SetupPreferences(
                alias = preferences[PreferenceKeys.ALIAS] ?: "",
                gridSize = preferences[PreferenceKeys.GRID_SIZE] ?: 5,
                trackTime = preferences[PreferenceKeys.TRACK_TIME] ?: false
            )
        }

    override suspend fun updateAlias(name: String) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[PreferenceKeys.ALIAS] = name
            }
        }
    }

    override suspend fun updateGridSize(size: Int) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[PreferenceKeys.GRID_SIZE] = size
            }
        }
    }

    override suspend fun updateTrackTime(track: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[PreferenceKeys.TRACK_TIME] = track
            }
        }
    }

    override suspend fun clearData() {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                remove(PreferenceKeys.ALIAS)
                remove(PreferenceKeys.GRID_SIZE)
                remove(PreferenceKeys.TRACK_TIME)
            }
        }
    }
}