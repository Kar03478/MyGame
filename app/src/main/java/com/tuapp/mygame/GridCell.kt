@file:OptIn(ExperimentalFoundationApi::class)

package com.tuapp.mygame

import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.unit.dp

@Composable
fun GridCell(
    cell: CellState,
    onDrop: (trayIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // highlight when drag is over it
    var isHovered by remember { mutableStateOf(false) }
    val dropTarget = remember(cell.piece) {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                isHovered = false
            }

            override fun onEntered(event: DragAndDropEvent) {
                isHovered = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isHovered = false
            }

            override fun onEnded(event: DragAndDropEvent) {
                isHovered = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val indexStr = event.toAndroidDragEvent()
                    .clipData
                    .getItemAt(0)
                    .text
                    .toString()
                val trayIndex = indexStr.toIntOrNull() ?: return false
                onDrop(trayIndex)
                return true
            }
        }
    }
    val backColor = when {
        isHovered -> MaterialTheme.colorScheme.primaryContainer
        else      -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backColor, RoundedCornerShape(6.dp))
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    // Filtra: solo eventos con nuestro label en sitios vacios
                    val isEmpty = cell.piece == null
                    isEmpty && event.toAndroidDragEvent()
                        .clipDescription
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dropTarget
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.piece != null) {
            CakePieceView(
                piece = cell.piece,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
    }
}