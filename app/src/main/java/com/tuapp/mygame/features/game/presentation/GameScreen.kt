package com.tuapp.mygame.features.game.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.R


@Composable
fun GameScreen(
    vm: GameViewModel = viewModel(),
    onBack: () -> Unit = {},
    onGameOver: () -> Unit = {}
) {
    val cells     by vm.cells.collectAsStateWithLifecycle()
    val tray      by vm.tray.collectAsStateWithLifecycle()
    val rows      by vm.rows.collectAsStateWithLifecycle()
    val cols      by vm.cols.collectAsStateWithLifecycle()
    val score     by vm.score.collectAsStateWithLifecycle()
    val isGameOver by vm.isGameOver.collectAsStateWithLifecycle()
    val hasWon    by vm.hasWon.collectAsStateWithLifecycle()
    val trackTime by vm.trackTime.collectAsStateWithLifecycle()
    val timeLeft  by vm.timeLeft.collectAsStateWithLifecycle()
    val isTimeUp  by vm.isTimeUp.collectAsStateWithLifecycle()

    // Navega a GameOverScreen cuando termina la partida
    LaunchedEffect(isGameOver, hasWon) {
        if (isGameOver || hasWon) onGameOver()
    }

    GameScreenContent(
        cells = cells,
        tray = tray,
        rows = rows,
        cols = cols,
        score = score,
        trackTime = trackTime,
        timeLeft = timeLeft,
        isTimeUp = isTimeUp,
        onDrop = { trayIndex, row, col ->
            vm.placePiece(trayIndex, row, col)
        }
    )
}

@Composable
private fun GameScreenContent(
    cells: List<com.tuapp.mygame.features.game.model.CellState>,
    tray: List<com.tuapp.mygame.features.game.model.CakePiece>,
    rows: Int,
    cols: Int,
    score: Int,
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    onDrop: (Int, Int, Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val boardAspect = cols.toFloat() / rows.toFloat()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(cols),
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(boardAspect),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(cells) { _, cell ->
                                GridCell(
                                    cell = cell,
                                    onDrop = { trayIndex ->
                                        onDrop(trayIndex, cell.row, cell.col)
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.screen_game),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        GameHeader(
                            trackTime = trackTime,
                            timeLeft = timeLeft,
                            isTimeUp = isTimeUp,
                            score = score
                        )

                        Spacer(Modifier.height(16.dp))

                        GameTray(pieces = tray)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.screen_game),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    GameHeader(
                        trackTime = trackTime,
                        timeLeft = timeLeft,
                        isTimeUp = isTimeUp,
                        score = score
                    )

                    Spacer(Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(cells) { _, cell ->
                            GridCell(
                                cell = cell,
                                onDrop = { trayIndex ->
                                    onDrop(trayIndex, cell.row, cell.col)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    GameTray(
                        pieces = tray
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHeader(
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    score: Int
) {
    if (trackTime) {
        Text(
            text = "%02d:%02d".format(timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                isTimeUp -> MaterialTheme.colorScheme.error
                timeLeft <= 10 -> MaterialTheme.colorScheme.error
                timeLeft <= 30 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }

    Text(
        text = stringResource(R.string.game_score, score, GameViewModel.TARGET_SCORE),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
