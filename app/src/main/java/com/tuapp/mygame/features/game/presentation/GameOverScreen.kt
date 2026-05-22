package com.tuapp.mygame.features.game.presentation

import android.content.res.Configuration
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R
import com.tuapp.mygame.core.ui.components.AppTopBar
import com.tuapp.mygame.features.game.model.EndReason
import com.tuapp.mygame.features.game.model.GameLog

@Composable
fun GameOverScreen(
    log: GameLog,
    onPlayAgain: () -> Unit,
    onSetup: () -> Unit,
    onQuit: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
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

    val durationText = formatDuration(log.durationSeconds)
    val resultEmoji = when (log.endReason) {
        EndReason.WIN        -> stringResource(R.string.game_over_win_emoji)
        EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_emoji)
        EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_emoji)
    }
    val resultHeading = when (log.endReason) {
        EndReason.WIN        -> stringResource(R.string.game_over_win_heading)
        EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_heading)
        EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_heading)
    }
    val resultMessage = when (log.endReason) {
        EndReason.WIN        -> stringResource(R.string.game_over_win_message, log.alias)
        EndReason.TIME_UP    -> stringResource(R.string.game_over_time_up_message, log.alias)
        EndReason.BOARD_FULL -> stringResource(R.string.game_over_board_full_message, log.alias)
    }
    val resultValue = when (log.endReason) {
        EndReason.WIN        -> stringResource(R.string.game_over_result_win)
        EndReason.TIME_UP    -> stringResource(R.string.game_over_result_time_up)
        EndReason.BOARD_FULL -> stringResource(R.string.game_over_result_board_full)
    }
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                actions = {
                    IconButton(onClick = onSetup) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.setup_settings)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameOverSummary(
                        log = log,
                        durationText = durationText,
                        resultEmoji = resultEmoji,
                        resultHeading = resultHeading,
                        resultMessage = resultMessage,
                        resultValue = resultValue,
                        compact = true,
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    )
                    GameOverActions(
                        recipientEmail = recipientEmail,
                        onRecipientEmailChange = { recipientEmail = it },
                        onSendEmail = { sendEmail() },
                        onPlayAgain = onPlayAgain,
                        onQuit = onQuit,
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight(),
                        centerVertically = true
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameOverSummary(
                        log = log,
                        durationText = durationText,
                        resultEmoji = resultEmoji,
                        resultHeading = resultHeading,
                        resultMessage = resultMessage,
                        resultValue = resultValue
                    )
                    HorizontalDivider()
                    GameOverActions(
                        recipientEmail = recipientEmail,
                        onRecipientEmailChange = { recipientEmail = it },
                        onSendEmail = { sendEmail() },
                        onPlayAgain = onPlayAgain,
                        onQuit = onQuit
                    )
                }
            }
        }
    }
}

@Composable
private fun GameOverSummary(
    log: GameLog,
    durationText: String,
    resultEmoji: String,
    resultHeading: String,
    resultMessage: String,
    resultValue: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_game_over),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = resultEmoji,
            style = if (compact) MaterialTheme.typography.displayMedium else MaterialTheme.typography.displayLarge
        )

        Text(
            text = resultHeading,
            style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = resultMessage,
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
            value = durationText
        )
        GameOverRow(
            label = stringResource(R.string.game_over_result_label),
            value = resultValue
        )
    }
}

@Composable
private fun GameOverActions(
    recipientEmail: String,
    onRecipientEmailChange: (String) -> Unit,
    onSendEmail: () -> Unit,
    onPlayAgain: () -> Unit,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier,
    centerVertically: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = if (centerVertically) {
            Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        } else {
            Arrangement.spacedBy(16.dp)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = recipientEmail,
            onValueChange = onRecipientEmailChange,
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
            onClick = onSendEmail,
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

@Composable
private fun GameOverRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
