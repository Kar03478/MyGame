package com.tuapp.mygame.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 48.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.home_logo_content_description),
                    modifier = Modifier
                        .size(500.dp)
                        .padding(bottom = 16.dp)
                )
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
    }
}
