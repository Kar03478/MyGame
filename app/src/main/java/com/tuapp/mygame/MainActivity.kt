package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.tuapp.mygame.core.theme.MyGameTheme
import com.tuapp.mygame.features.game.presentation.GameViewModel
import com.tuapp.mygame.features.db.AppDatabase


class MainActivity : ComponentActivity() {
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
                    dao = gameLogDao,
                    onQuit = { finish() }
                )
            }
        }
    }
}
