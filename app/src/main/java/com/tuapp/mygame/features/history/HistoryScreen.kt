package com.tuapp.mygame.features.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R
import com.tuapp.mygame.core.ui.components.AppTopBar
import com.tuapp.mygame.features.db.GameLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HistoryScreen(
    logs: List<GameLogEntity>,
    onBack: () -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()
    var selectedLogId by rememberSaveable { mutableIntStateOf(logs.firstOrNull()?.id ?: 0) }

    LaunchedEffect(logs, windowSizeClass) {
        if (logs.isNotEmpty() && logs.none { it.id == selectedLogId }) {
            selectedLogId = logs.first().id
        }
    }

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.history_screen),
                onBack = {
                    if (navigator.canNavigateBack()) {
                        scope.launch { navigator.navigateBack() }
                    } else {
                        onBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        if (logs.isEmpty()) {
            EmptyHistory(modifier = Modifier.fillMaxSize().padding(innerPadding))
        } else {
            val selectedLog = logs.firstOrNull { it.id == selectedLogId } ?: logs.first()
            NavigableListDetailPaneScaffold(
                navigator = navigator,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                listPane = {
                    AnimatedPane {
                        HistoryList(
                            logs = logs,
                            selectedLogId = selectedLog.id,
                            onLogSelected = { log ->
                                selectedLogId = log.id
                                scope.launch {
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Detail,
                                        contentKey = log.id
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        HistoryDetail(
                            log = selectedLog,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.history_empty),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryList(
    logs: List<GameLogEntity>,
    selectedLogId: Int,
    onLogSelected: (GameLogEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs, key = { it.id }) { log ->
            HistoryListItem(
                log = log,
                selected = log.id == selectedLogId,
                onClick = { onLogSelected(log) }
            )
        }
    }
}

@Composable
private fun HistoryListItem(
    log: GameLogEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shortDate = remember(log.timestamp) {
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(log.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(log.alias, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = localizedResult(log.result),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(shortDate, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HistoryDetail(
    log: GameLogEntity,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        HistoryDetailContent(
            log = log,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun HistoryDetailContent(
    log: GameLogEntity,
    modifier: Modifier = Modifier
) {
    val date = remember(log.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(log.timestamp))
    }
    val duration = formatDuration(log.durationSeconds)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = log.alias,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        HistoryDetailRow(
            label = stringResource(R.string.history_date_label),
            value = date
        )
        HistoryDetailRow(
            label = stringResource(R.string.history_score_label),
            value = stringResource(R.string.history_score_value, log.score)
        )
        HistoryDetailRow(
            label = stringResource(R.string.history_board_label),
            value = stringResource(R.string.history_board_value, log.rows, log.cols)
        )
        HistoryDetailRow(
            label = stringResource(R.string.history_duration_label),
            value = duration
        )
        HistoryDetailRow(
            label = stringResource(R.string.history_result_label),
            value = localizedResult(log.result)
        )
    }
}

@Composable
private fun HistoryDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun formatDuration(durationSeconds: Long): String {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    return if (minutes > 0) {
        stringResource(R.string.history_duration_minutes_value, minutes, seconds)
    } else {
        stringResource(R.string.history_duration_seconds_value, seconds)
    }
}

@Composable
private fun localizedResult(result: String): String {
    return when (result) {
        "WIN" -> stringResource(R.string.game_over_result_win)
        "TIME_UP" -> stringResource(R.string.game_over_result_time_up)
        "BOARD_FULL" -> stringResource(R.string.game_over_result_board_full)
        else -> result
    }
}
