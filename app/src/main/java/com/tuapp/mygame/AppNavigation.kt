package com.tuapp.mygame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.tuapp.mygame.features.game.presentation.GameOverScreen
import com.tuapp.mygame.features.game.presentation.GameScreen
import com.tuapp.mygame.features.game.presentation.GameViewModel
import com.tuapp.mygame.features.help.HelpScreen
import com.tuapp.mygame.features.home.HomeScreen
import com.tuapp.mygame.features.setup.SetupScreen
import com.tuapp.mygame.features.db.GameLogDao
import com.tuapp.mygame.features.history.HistoryScreen

@Composable
fun AppNavigation(vm: GameViewModel, dao: GameLogDao, onQuit: () -> Unit) {
    val navController = rememberNavController()
    val logs by dao.getAllLogs().collectAsState(initial = emptyList())


    val navGraph = remember(navController) {
        navController.createGraph(startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onPlay = {
                        navController.navigate(Setup) {
                            popUpTo(Home) { inclusive = true }
                        }
                    },
                    onHelp = { navController.navigate(Help) },
                    onHistory = { navController.navigate(History) },
                    onQuit = onQuit
                )
            }
            composable<Setup> {
                SetupScreen(
                    onStartGame = { alias, rows, cols, trackTime ->
                        vm.startGame(alias, rows, cols, trackTime)
                        navController.navigate(Game) {
                            popUpTo(Setup) { inclusive = true }
                        }
                    }
                )
            }
            composable<Game> {
                GameScreen(
                    vm = vm,
                    onBack = { vm.abandonGame() },
                    onGameOver = {
                        vm.saveGameLog()
                        navController.navigate(GameOver)
                    }
                )
            }
            composable<GameOver> {
                GameOverScreen(
                    log = vm.buildGameLog(),
                    onPlayAgain = {
                        vm.resetGame()
                        navController.navigate(Setup) {
                            popUpTo(Game) { inclusive = true }
                        }
                    },
                    onQuit = onQuit
                )
            }
            composable<Help> {
                HelpScreen(onBack = { navController.popBackStack() })
            }
            composable<History> {
                HistoryScreen(
                    logs = logs,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    NavHost(navController = navController, graph = navGraph)
}