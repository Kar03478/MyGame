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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.R
import com.tuapp.mygame.features.game.model.CakePiece
import com.tuapp.mygame.features.game.model.CellState
import com.tuapp.mygame.features.game.model.GameEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.window.core.layout.WindowSizeClass


@Composable
fun GameScreen(
    vm: GameViewModel = viewModel(),
    onGameOver: () -> Unit = {}
) {
    val cells     by vm.cells.collectAsStateWithLifecycle()
    val tray      by vm.tray.collectAsStateWithLifecycle()
    val rows      by vm.rows.collectAsStateWithLifecycle()
    val cols      by vm.cols.collectAsStateWithLifecycle()
    val score     by vm.score.collectAsStateWithLifecycle()
    val alias     by vm.alias.collectAsStateWithLifecycle()
    val isGameOver by vm.isGameOver.collectAsStateWithLifecycle()
    val hasWon    by vm.hasWon.collectAsStateWithLifecycle()
    val trackTime by vm.trackTime.collectAsStateWithLifecycle()
    val timeLeft  by vm.timeLeft.collectAsStateWithLifecycle()
    val isTimeUp  by vm.isTimeUp.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle()

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
        alias = alias,
        trackTime = trackTime,
        timeLeft = timeLeft,
        isTimeUp = isTimeUp,
        isGameOver = isGameOver,
        hasWon = hasWon,
        events = events,
        onDrop = { trayIndex, row, col ->
            vm.placePiece(trayIndex, row, col)
        }
    )
}

@Composable
private fun GameScreenContent(
    cells: List<CellState>,
    tray: List<CakePiece>,
    rows: Int,
    cols: Int,
    score: Int,
    alias: String,
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    isGameOver: Boolean,
    hasWon: Boolean,
    events: List<GameEvent>,
    onDrop: (Int, Int, Int) -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val showSecondaryPane = windowSizeClass.isAtLeastBreakpoint(
        widthDpBreakpoint = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        heightDpBreakpoint = WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
    )
    val isExpandedWidth = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val logPaneWidth = if (isExpandedWidth) 320.dp else 260.dp
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
            if (showSecondaryPane) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GamePlayArea(
                        cells = cells,
                        tray = tray,
                        cols = cols,
                        boardAspect = boardAspect,
                        trackTime = trackTime,
                        timeLeft = timeLeft,
                        isTimeUp = isTimeUp,
                        score = score,
                        onDrop = onDrop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    VerticalDivider()

                    LiveGameLogPanel(
                        alias = alias,
                        rows = rows,
                        cols = cols,
                        score = score,
                        trackTime = trackTime,
                        timeLeft = timeLeft,
                        isTimeUp = isTimeUp,
                        isGameOver = isGameOver,
                        hasWon = hasWon,
                        occupiedCells = cells.count { it.piece != null },
                        totalCells = cells.size,
                        events = events,
                        modifier = Modifier
                            .width(logPaneWidth)
                            .fillMaxHeight()
                    )
                }
            } else if (isLandscape) {
                PhoneLandscapeGameArea(
                    cells = cells,
                    tray = tray,
                    cols = cols,
                    boardAspect = boardAspect,
                    trackTime = trackTime,
                    timeLeft = timeLeft,
                    isTimeUp = isTimeUp,
                    score = score,
                    onDrop = onDrop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                GamePlayArea(
                    cells = cells,
                    tray = tray,
                    cols = cols,
                    boardAspect = boardAspect,
                    trackTime = trackTime,
                    timeLeft = timeLeft,
                    isTimeUp = isTimeUp,
                    score = score,
                    onDrop = onDrop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PhoneLandscapeGameArea(
    cells: List<CellState>,
    tray: List<CakePiece>,
    cols: Int,
    boardAspect: Float,
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    score: Int,
    onDrop: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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
                .width(240.dp)
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
}

@Composable
private fun GamePlayArea(
    cells: List<CellState>,
    tray: List<CakePiece>,
    cols: Int,
    boardAspect: Float,
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    score: Int,
    onDrop: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.screen_game),
            style = MaterialTheme.typography.titleMedium
        )

        GameHeader(
            trackTime = trackTime,
            timeLeft = timeLeft,
            isTimeUp = isTimeUp,
            score = score
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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

        GameTray(pieces = tray)
    }
}

@Composable
private fun LiveGameLogPanel(
    alias: String,
    rows: Int,
    cols: Int,
    score: Int,
    trackTime: Boolean,
    timeLeft: Long,
    isTimeUp: Boolean,
    isGameOver: Boolean,
    hasWon: Boolean,
    occupiedCells: Int,
    totalCells: Int,
    events: List<GameEvent>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(events.size) {
        if (events.isNotEmpty()) listState.animateScrollToItem(events.size - 1)
    }

    val result = when {
        hasWon -> stringResource(R.string.game_over_result_win)
        isTimeUp -> stringResource(R.string.game_over_result_time_up)
        isGameOver -> stringResource(R.string.game_over_result_board_full)
        else -> stringResource(R.string.game_log_status_in_progress)
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.game_log_live_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            GameLogRow(
                label = stringResource(R.string.game_log_player_label),
                value = alias.ifBlank { stringResource(R.string.alias) }
            )
            GameLogRow(
                label = stringResource(R.string.game_over_score_label),
                value = stringResource(R.string.game_over_score_value, score)
            )
            GameLogRow(
                label = stringResource(R.string.game_over_board_label),
                value = stringResource(R.string.game_log_board_value, rows, cols)
            )
            GameLogRow(
                label = stringResource(R.string.game_log_cells_label),
                value = stringResource(R.string.game_log_cells_value, occupiedCells, totalCells)
            )
            GameLogRow(
                label = stringResource(R.string.game_log_timer_label),
                value = if (trackTime) {
                    "%02d:%02d".format(timeLeft / 60, timeLeft % 60)
                } else {
                    stringResource(R.string.game_log_no_timer)
                }
            )
            GameLogRow(
                label = stringResource(R.string.game_over_result_label),
                value = result
            )

            HorizontalDivider()
            Text(
                text = stringResource(R.string.game_log_events_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(events) { event ->
                    val time = remember(event.timestamp) {
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(Date(event.timestamp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alignByBaseline()
                        )
                        Text(
                            text = event.message,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameLogRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
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
