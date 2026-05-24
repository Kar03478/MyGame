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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MyApp

        setContent {
            MyGameTheme {
                val vm: GameViewModel = viewModel()
                vm.setDao(app.db.gameLogDao())
                AppNavigation(
                    vm = vm,
                    setupRepository = app.setupRepository,
                    dao = app.db.gameLogDao(),
                    onQuit = { finish() }
                )
            }
        }
    }
}
