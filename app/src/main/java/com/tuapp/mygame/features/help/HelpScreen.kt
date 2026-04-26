package com.tuapp.mygame.features.help

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R
import com.tuapp.mygame.core.ui.components.AppTopBar

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.help_title),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.help_how_to_play), style = MaterialTheme.typography.headlineMedium)

            HelpSection(
                title = stringResource(R.string.help_goal_title),
                body = stringResource(R.string.help_goal_body)
            )
            HelpSection(
                title = stringResource(R.string.help_pieces_title),
                body = stringResource(R.string.help_pieces_body)
            )
            HelpSection(
                title = stringResource(R.string.help_place_title),
                body = stringResource(R.string.help_place_body)
            )
            HelpSection(
                title = stringResource(R.string.help_merge_title),
                body = stringResource(R.string.help_merge_body)
            )
            HelpSection(
                title = stringResource(R.string.help_complete_title),
                body = stringResource(R.string.help_complete_body)
            )
            HelpSection(
                title = stringResource(R.string.help_timer_title),
                body = stringResource(R.string.help_timer_body)
            )
            HelpSection(
                title = stringResource(R.string.help_game_over_title),
                body = stringResource(R.string.help_game_over_body)
            )
        }
    }
}

@Composable
private fun HelpSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(body, style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider()
    }
}
