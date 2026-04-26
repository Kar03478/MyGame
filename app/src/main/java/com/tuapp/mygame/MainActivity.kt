package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.core.theme.MyGameTheme
import com.tuapp.mygame.features.game.presentation.GameOverScreen
import com.tuapp.mygame.features.game.presentation.GameScreen
import com.tuapp.mygame.features.game.presentation.GameViewModel
import com.tuapp.mygame.features.help.HelpScreen
import com.tuapp.mygame.features.home.HomeScreen
import com.tuapp.mygame.features.setup.SetupScreen

enum class Screen { HOME, SETUP, GAME, GAME_OVER, HELP }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                val vm: GameViewModel = viewModel()
                var screen by rememberSaveable { mutableStateOf(Screen.HOME) }

                when (screen) {
                    Screen.HOME -> HomeScreen(
                        onPlay = { screen = Screen.SETUP },
                        onHelp = { screen = Screen.HELP },
                        onQuit = { finish() }
                    )
                    Screen.SETUP -> SetupScreen(
                        onStartGame = { alias, rows, cols, trackTime ->
                            vm.startGame(alias, rows, cols, trackTime)
                            screen = Screen.GAME
                        }
                    )
                    Screen.GAME -> GameScreen(
                        vm = vm,
                        onBack = {
                            vm.abandonGame()
                        },
                        onGameOver = { screen = Screen.GAME_OVER }
                    )
                    Screen.GAME_OVER -> GameOverScreen (
                        log = vm.buildGameLog(),
                        onPlayAgain = {
                            vm.resetGame()
                            screen = Screen.SETUP
                        },
                        onQuit = { finish() }
                    )
                    Screen.HELP -> HelpScreen(
                        onBack = { screen = Screen.HOME }
                    )
                }
            }
        }
    }
}
