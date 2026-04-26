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
import com.tuapp.mygame.state.GameViewModel
import com.tuapp.mygame.ui.game.Game
import com.tuapp.mygame.ui.setup.SetupScreen
import com.tuapp.mygame.ui.theme.MyGameTheme

enum class Screen { HOME, SETUP, GAME, HELP }
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                val vm: GameViewModel = viewModel()
                var screen by remember { mutableStateOf(Screen.HOME) }

                when (screen) {
                    Screen.HOME  -> HomeScreen(
                        onPlay = { screen = Screen.SETUP },
                        onHelp = { screen = Screen.HELP },
                        onQuit = { finish() }
                    )
                    Screen.SETUP -> SetupScreen(
                        onBack = { screen = Screen.HOME },
                        onStartGame = { alias, rows, cols, trackTime ->
                            vm.startGame(alias, rows, cols, trackTime)
                            screen = Screen.GAME
                        }
                    )
                    Screen.GAME  -> Game(
                        vm = vm,
                        onBack = {
                            vm.resetGame()
                            screen = Screen.SETUP
                        }
                    )
                    Screen.HELP  -> HelpScreen(
                        onBack = { screen = Screen.HOME }
                    )
                }
            }
        }
    }
}
