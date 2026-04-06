package com.tuapp.mygame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

data class DragInfo(
    val piece: CakePiece,
    val trayIndex: Int,
    val offset: Offset
)

val LocalDragState = compositionLocalOf<MutableState<DragInfo?>> {
    error("DragState not provided")
}

@Composable
fun DragStateProvider(content: @Composable () -> Unit) {
    val drawerState = remember { mutableStateOf<DragInfo?>(null)}
    CompositionLocalProvider(LocalDragState provides drawerState, content = content)
}