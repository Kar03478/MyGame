package com.tuapp.mygame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.ui.components.AppTopBar

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Ayuda",
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
            Text("¿Cómo se juega?", style = MaterialTheme.typography.headlineMedium)

            HelpSection(
                title = "🎯 Objetivo",
                body = "Completa pasteles juntando 4 trozos del mismo sabor en el tablero. Cada pastel completado suma 10 puntos."
            )
            HelpSection(
                title = "🍰 Los trozos",
                body = "En la bandeja inferior siempre hay 3 trozos de pastel. Cada trozo tiene un sabor (color) y una cantidad de porciones (1, 2 o 3 de 4)."
            )
            HelpSection(
                title = "👆 Cómo colocar",
                body = "Mantén pulsado un trozo de la bandeja y arrástralo hasta una celda vacía del tablero. Solo puedes colocar en celdas vacías."
            )
            HelpSection(
                title = "🔗 Fusión",
                body = "Al colocar un trozo, si hay trozos del mismo sabor adyacentes (arriba, abajo, izquierda o derecha), se fusionan automáticamente. Si la suma supera 4, el sobrante se queda en la celda donde colocaste."
            )
            HelpSection(
                title = "💥 Pastel completo",
                body = "Cuando un grupo llega a 4 trozos, el pastel se completa con una animación y desaparece del tablero sumando 10 puntos."
            )
            HelpSection(
                title = "⏱️ Control de tiempo",
                body = "Si activas el control de tiempo en la configuración, tendrás 2 minutos para conseguir la máxima puntuación. El contador se pone rojo cuando quedan 10 segundos."
            )
            HelpSection(
                title = "💀 Game Over",
                body = "La partida termina cuando el tablero está completamente lleno y no puedes colocar más trozos, o cuando se acaba el tiempo si lo tienes activado."
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
