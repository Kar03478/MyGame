package com.tuapp.mygame.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.mygame.state.GameViewModel
import com.tuapp.mygame.ui.components.AppTopBar


@Composable
fun Game(
    vm: GameViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val cells      by vm.cells.collectAsStateWithLifecycle()
    val tray       by vm.tray.collectAsStateWithLifecycle()
    val cols       by vm.cols.collectAsStateWithLifecycle()
    val score      by vm.score.collectAsStateWithLifecycle()
    val alias      by vm.alias.collectAsStateWithLifecycle()
    val isGameOver by vm.isGameOver.collectAsStateWithLifecycle()
    val hasWon     by vm.hasWon.collectAsStateWithLifecycle()
    val trackTime by vm.trackTime.collectAsStateWithLifecycle()
    val timeLeft  by vm.timeLeft.collectAsStateWithLifecycle()
    val isTimeUp  by vm.isTimeUp.collectAsStateWithLifecycle()


    fun formatTime(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = alias.ifBlank { "Partida" },
                onBack = onBack
            )
        }
    ) { innerPadding ->

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (trackTime) {
                    Text(
                        text = formatTime(timeLeft),
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
                    text = "Puntos: $score / ${GameViewModel.TARGET_SCORE}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Objetivo: ${GameViewModel.TARGET_SCORE} puntos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                vm.placePiece(trayIndex, cell.row, cell.col)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                GameTray(
                    pieces = tray,
                )
            }

            // Game Over overlay
            if (isGameOver || hasWon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (hasWon) "\uD83C\uDF89" else "\uD83D\uDE35",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = if (hasWon) "¡Has ganado!" else "¡Has perdido!",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = when {
                                    hasWon -> alias.ifBlank { "Jugador" } + ", has llegado a los ${GameViewModel.TARGET_SCORE} puntos."
                                    isTimeUp -> alias.ifBlank { "Jugador" } + ", se ha acabado el tiempo."
                                    else -> alias.ifBlank { "Jugador" } + ", el tablero está lleno."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Puntuación final: $score",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    vm.resetGame()
                                    onBack()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volver a jugar")
                            }
                        }
                    }
                }
            }
        }
    }
}

