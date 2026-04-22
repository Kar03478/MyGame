package com.tuapp.mygame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(onStartGame: (alias: String, rows: Int, cols: Int) -> Unit) {
    var alias by rememberSaveable { mutableStateOf("") }
    var rows   by rememberSaveable { mutableIntStateOf(5) }
    var cols   by rememberSaveable { mutableIntStateOf(5) }
    var trackTime by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nueva partida", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        // Alias
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Tamaño de la parrilla
        Text("Filas: $rows", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = rows.toFloat(),
            onValueChange = { rows = it.toInt() },
            valueRange = 2f..10f,
            steps = 7,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text("Columnas: $cols", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = cols.toFloat(),
            onValueChange = { cols = it.toInt() },
            valueRange = 2f..10f,
            steps = 7,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Control de tiempo (deshabilitado por ahora)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                checked = trackTime,
                onCheckedChange = { trackTime = it },
                enabled = false   // lo activaremos más adelante
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Control de tiempo")
                Text(
                    "Próximamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onStartGame(alias, rows, cols) },
            enabled = alias.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}