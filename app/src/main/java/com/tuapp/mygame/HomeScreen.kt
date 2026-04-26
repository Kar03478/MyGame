package com.tuapp.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.ui.components.AppTopBar

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onHelp: () -> Unit,
    onQuit: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(title = "MyGame")
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
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo Pastelitos",
                    modifier = Modifier
                        .size(500.dp)
                        .padding(bottom = 16.dp)
                )
                Button(
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¡Jugar!", style = MaterialTheme.typography.titleMedium)
                }

                OutlinedButton(
                    onClick = onHelp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ayuda", style = MaterialTheme.typography.titleMedium)
                }

                TextButton(
                    onClick = onQuit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salir", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
