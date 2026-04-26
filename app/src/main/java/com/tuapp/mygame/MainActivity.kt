package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.ui.theme.MyGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                val vm: GameViewModel = viewModel()
                var gameStarted by remember { mutableStateOf(false) }

                if (gameStarted) {
                    Game(
                        vm = vm,
                        onBack = { gameStarted = false }
                    )
                } else {
                    SetupScreen(
                        onStartGame = { alias, rows, cols ->
                            vm.startGame(alias, rows, cols)
                            gameStarted = true
                        }
                    )
                }
            }
        }
    }
}

data class CellState(
    val row: Int,
    val col: Int,
    val piece: CakePiece? = null
)


