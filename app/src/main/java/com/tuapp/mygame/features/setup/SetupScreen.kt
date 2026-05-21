package com.tuapp.mygame.features.setup

import android.content.res.Configuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R

@Composable
fun SetupScreen(
    onStartGame: (alias: String, rows: Int, cols: Int, trackTime: Boolean) -> Unit
) {
    val context = LocalContext.current
    var alias by rememberSaveable { mutableStateOf(context.getString(R.string.alias)) }
    var trackTime by rememberSaveable { mutableStateOf(false) }
    var gridSize by rememberSaveable { mutableIntStateOf(5) }

    SetupScreenContent(
        alias = alias,
        trackTime = trackTime,
        gridSize = gridSize,
        onAliasChange = { alias = it },
        onGridSizeSelected = { size ->
            gridSize = size
        },
        onTrackTimeChange = { trackTime = it },
        onStartGame = { onStartGame(alias, gridSize, gridSize, trackTime) }
    )
}

@Composable
private fun SetupScreenContent(
    alias: String,
    trackTime: Boolean,
    gridSize: Int,
    onAliasChange: (String) -> Unit,
    onGridSizeSelected: (Int) -> Unit,
    onTrackTimeChange: (Boolean) -> Unit,
    onStartGame: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.screen_setup),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = alias,
                onValueChange = onAliasChange,
                label = { Text(stringResource(R.string.setup_name)) },
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

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BoardSizeSelector(
                        gridSize = gridSize,
                        onGridSizeSelected = onGridSizeSelected,
                        modifier = Modifier.weight(1f)
                    )
                    TimeControlSwitch(
                        trackTime = trackTime,
                        onTrackTimeChange = onTrackTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                BoardSizeSelector(
                    gridSize = gridSize,
                    onGridSizeSelected = onGridSizeSelected
                )
                TimeControlSwitch(
                    trackTime = trackTime,
                    onTrackTimeChange = onTrackTimeChange
                )
            }

            Button(
                onClick = onStartGame,
                enabled = alias.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.setup_start))
            }
        }
    }
}

@Composable
private fun BoardSizeSelector(
    gridSize: Int,
    onGridSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.setup_board_size), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3, 4, 5).forEach { size ->
                FilterChip(
                    selected = gridSize == size,
                    onClick = { onGridSizeSelected(size) },
                    label = { Text(stringResource(R.string.setup_grid_size, size)) }
                )
            }
        }
    }
}

@Composable
private fun TimeControlSwitch(
    trackTime: Boolean,
    onTrackTimeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Switch(
            checked = trackTime,
            onCheckedChange = onTrackTimeChange,
            enabled = true
        )
        Spacer(Modifier.width(12.dp))

        Text(stringResource(R.string.setup_time_control))
    }
}
