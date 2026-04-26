package com.tuapp.mygame.features.game.presentation

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R
import com.tuapp.mygame.features.game.model.EndReason
import com.tuapp.mygame.features.game.model.GameLog

@Composable
fun GameOverScreen(
    log: GameLog,
    onPlayAgain: () -> Unit,
    onQuit: () -> Unit
) {
    val context = LocalContext.current
    var recipientEmail by rememberSaveable { mutableStateOf("") }

    fun formatDuration(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    fun buildEmailBody() = context.getString(
        R.string.game_over_email_body,
        log.alias,
        log.score,
        log.gridSize,
        formatDuration(log.durationSeconds),
        when (log.endReason) {
            EndReason.WIN        -> context.getString(R.string.game_over_result_win)
            EndReason.TIME_UP    -> context.getString(R.string.game_over_result_time_up)
            EndReason.BOARD_FULL -> context.getString(R.string.game_over_result_board_full)
        }
    )

    fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            if (recipientEmail.isNotBlank()) {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            }
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.game_over_email_subject, log.score))
            putExtra(Intent.EXTRA_TEXT, buildEmailBody())
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.game_over_email_chooser)))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.screen_game_over),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = when (log.endReason) {
                    EndReason.WIN        -> stringResource(R.string.game_over_win_emoji)
                    EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_emoji)
                    EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_emoji)
                },
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = when (log.endReason) {
                    EndReason.WIN        -> stringResource(R.string.game_over_win_heading)
                    EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_heading)
                    EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_heading)
                },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (log.endReason) {
                    EndReason.WIN        -> stringResource(R.string.game_over_win_message, log.alias)
                    EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_message, log.alias)
                    EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_message, log.alias)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            HorizontalDivider()

            GameOverRow(
                label = stringResource(R.string.game_over_score_label),
                value = stringResource(R.string.game_over_score_value, log.score)
            )
            GameOverRow(
                label = stringResource(R.string.game_over_board_label),
                value = stringResource(R.string.game_over_board_value, log.gridSize)
            )
            GameOverRow(
                label = stringResource(R.string.game_over_duration_label),
                value = formatDuration(log.durationSeconds)
            )
            GameOverRow(
                label = stringResource(R.string.game_over_result_label),
                value = when (log.endReason) {
                    EndReason.WIN        -> stringResource(R.string.game_over_result_win)
                    EndReason.TIME_UP    -> stringResource(R.string.game_over_result_time_up)
                    EndReason.BOARD_FULL -> stringResource(R.string.game_over_result_board_full)
                }
            )

            HorizontalDivider()

            OutlinedTextField(
                value = recipientEmail,
                onValueChange = { recipientEmail = it },
                label = { Text(stringResource(R.string.game_over_email_recipient)) },
                placeholder = { Text("correo@ejemplo.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { sendEmail() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.game_over_send_email))
            }

            OutlinedButton(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.game_over_play_again))
            }

            TextButton(
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.home_quit))
            }
        }
    }
}

@Composable
private fun GameOverRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}