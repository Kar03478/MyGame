package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.core.theme.MyGameTheme
import com.tuapp.mygame.features.game.presentation.GameViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                val vm: GameViewModel = viewModel()
                AppNavigation(
                    vm = vm,
                    onQuit = { finish() }
                )
            }
        }
    }
}
