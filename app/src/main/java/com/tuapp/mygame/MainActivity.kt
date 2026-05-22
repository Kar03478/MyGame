package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.tuapp.mygame.core.theme.MyGameTheme
import com.tuapp.mygame.features.game.presentation.GameViewModel
import com.tuapp.mygame.features.db.AppDatabase
import com.tuapp.mygame.features.setup.SetupPreferencesRepo
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<androidx.datastore.preferences.core.Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
internal const val dataStoreFileName = "setup.preferences_pb"

class MainActivity : ComponentActivity() {
    private val setupRepository by lazy {
        val dataStore = createDataStore {
            applicationContext.filesDir.resolve(dataStoreFileName).absolutePath
        }
        SetupPreferencesRepo(dataStore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = remember {
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "mygame-db"
                ).build()
            }

            MyGameTheme {
                val vm: GameViewModel = viewModel()
                val gameLogDao = db.gameLogDao()
                vm.setDao(gameLogDao)
                AppNavigation(
                    vm = vm,
                    setupRepository = setupRepository,
                    dao = gameLogDao,
                    onQuit = { finish() }
                )
            }
        }
    }
}
