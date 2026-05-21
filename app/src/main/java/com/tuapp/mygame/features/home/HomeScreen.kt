package com.tuapp.mygame.features.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.R
import com.tuapp.mygame.core.ui.components.AppTopBar

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    onQuit: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(title = stringResource(R.string.app_name))
        }
    ) { innerPadding ->
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.home_logo_content_description),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .fillMaxHeight(0.85f)
                                .sizeIn(maxWidth = 320.dp, maxHeight = 320.dp)
                        )
                    }
                    HomeButtons(
                        onPlay = onPlay,
                        onHelp = onHelp,
                        onQuit = onQuit,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.home_logo_content_description),
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .sizeIn(maxWidth = 320.dp)
                            .aspectRatio(1f)
                            .padding(bottom = 16.dp)
                    )
                    HomeButtons(
                        onPlay = onPlay,
                        onHelp = onHelp,
                        onQuit = onQuit
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeButtons(
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_play), style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = onHelp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_help), style = MaterialTheme.typography.titleMedium)
        }

        TextButton(
            onClick = onQuit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_quit), style = MaterialTheme.typography.titleMedium)
        }
    }
}
