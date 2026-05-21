package com.tuapp.mygame.features.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.core.ui.components.AppTopBar
import com.tuapp.mygame.features.db.GameLogEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    logs: List<GameLogEntity>,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(title = "Historial", onBack = onBack)
        }
    ) { innerPadding ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Todavía no hay partidas guardadas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    GameLogItem(log)
                }
            }
        }
    }
}

@Composable
private fun GameLogItem(log: GameLogEntity) {
    var expanded by remember { mutableStateOf(false) }
    val date = remember(log.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(log.timestamp))
    }
    val duration = remember(log.durationSeconds) {
        val m = log.durationSeconds / 60
        val s = log.durationSeconds % 60
        if (m > 0) "${m}m ${s}s" else "${s}s"
    }
    val shortDate = remember(log.timestamp) {
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(log.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(log.alias, style = MaterialTheme.typography.titleMedium)
                Text(shortDate, style = MaterialTheme.typography.bodySmall)  // solo dd/MM
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    Text("Fecha: $date", style = MaterialTheme.typography.bodyMedium)  // fecha completa aquí
                    Text("Tablero: ${log.rows} × ${log.cols}", style = MaterialTheme.typography.bodyMedium)
                    Text("Duración: $duration", style = MaterialTheme.typography.bodyMedium)
                    Text("Resultado: ${log.result}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
