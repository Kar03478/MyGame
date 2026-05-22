package com.tuapp.mygame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuapp.mygame.features.game.presentation.GameOverScreen
import com.tuapp.mygame.features.game.presentation.GameScreen
import com.tuapp.mygame.features.game.presentation.GameViewModel
import com.tuapp.mygame.features.help.HelpScreen
import com.tuapp.mygame.features.home.HomeScreen
import com.tuapp.mygame.features.setup.SetupScreen
import com.tuapp.mygame.features.db.GameLogDao
import com.tuapp.mygame.features.history.HistoryScreen
import com.tuapp.mygame.features.setup.SetupPreferencesRepo
import com.tuapp.mygame.features.setup.SetupViewModel
import com.tuapp.mygame.features.setup.SetupViewModelFactory

@Composable
fun AppNavigation(
    vm: GameViewModel,
    setupRepository: SetupPreferencesRepo,
    dao: GameLogDao,
    onQuit: () -> Unit,
) {
    val navController = rememberNavController()
    val logs by dao.getAllLogs().collectAsState(initial = emptyList())

    val setupVm: SetupViewModel = viewModel(
        factory = SetupViewModelFactory(setupRepository)
    )
    val hasSetup by setupVm.hasCompletedSetup.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onPlay = {
                    if (hasSetup) {
                        val s = setupVm.state.value
                        vm.startGame(s.alias, s.gridSize, s.gridSize, s.trackTime)
                        navController.navigate(Game) {
                            popUpTo(Home) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Setup) {
                            popUpTo(Home) { inclusive = true }
                        }
                    }
                },
                onHelp = { navController.navigate(Help) },
                onHistory = { navController.navigate(History) },
                onSetup = { navController.navigate(Setup) },
                onQuit = onQuit
            )
        }
        composable<Setup> {
            SetupScreen(
                vm = setupVm,
                onStartGame = { alias, rows, cols, trackTime ->
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        vm.startGame(alias, rows, cols, trackTime)
                        navController.navigate(Game) {
                            popUpTo(Setup) { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
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
                    val s = setupVm.state.value
                    vm.startGame(s.alias, s.gridSize, s.gridSize, s.trackTime)
                    navController.navigate(Game) {
                        popUpTo(Game) { inclusive = true }
                    }
                },
                onSetup = { navController.navigate(Setup) },
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
