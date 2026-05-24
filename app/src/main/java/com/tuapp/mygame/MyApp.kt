package com.tuapp.mygame

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.tuapp.mygame.features.db.AppDatabase
import com.tuapp.mygame.features.setup.SetupPreferencesRepo
import okio.Path.Companion.toPath

class MyApp : Application() {

    val db: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "mygame-db").build()
    }

    val setupRepository: SetupPreferencesRepo by lazy {
        SetupPreferencesRepo(
            PreferenceDataStoreFactory.createWithPath {
                filesDir.resolve("setup.preferences_pb").absolutePath.toPath()
            }
        )
    }
}