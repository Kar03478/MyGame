package com.tuapp.mygame.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.ui.components.AppTopBar

@Composable
fun SetupScreen(
    onBack: () -> Unit = {},
    onStartGame: (alias: String, rows: Int, cols: Int, trackTime: Boolean) -> Unit
) {
    var alias by rememberSaveable { mutableStateOf("") }
    var rows   by rememberSaveable { mutableIntStateOf(5) }
    var cols   by rememberSaveable { mutableIntStateOf(5) }
    var trackTime by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Nueva partida",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )

            Spacer(Modifier.height(24.dp))

            Text("Filas: $rows", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = rows.toFloat(),
                onValueChange = { rows = it.toInt() },
                valueRange = 2f..5f,
                steps = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.tertiary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            Spacer(Modifier.height(8.dp))

            Text("Columnas: $cols", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = cols.toFloat(),
                onValueChange = { cols = it.toInt() },
                valueRange = 2f..5f,
                steps = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.tertiary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = trackTime,
                    onCheckedChange = { trackTime = it },
                    enabled = true
                )
                Spacer(Modifier.width(12.dp))

                Text("Control de tiempo")

            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onStartGame(alias, rows, cols, trackTime) },
                enabled = alias.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Empezar")
            }
        }
    }
}
