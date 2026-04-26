@file:OptIn(ExperimentalFoundationApi::class)

package com.tuapp.mygame

import android.content.ClipDescription
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun GridCell(
    cell: CellState,
    onDrop: (trayIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }

    LaunchedEffect(cell.piece) {
        if (cell.piece != null) isHovered = false
    }

    // Escala con rebote al aparecer una pieza nueva
    val pieceScale by animateFloatAsState(
        targetValue = if (cell.piece != null && !cell.isDisappearing) 1f else 0f,
        animationSpec = if (cell.piece != null && !cell.isDisappearing)
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        else
            tween(durationMillis = 280, easing = EaseIn),
        label = "pieceScale"
    )

    val pieceAlpha by animateFloatAsState(
        targetValue = if (cell.isDisappearing) 0f else 1f,
        animationSpec = tween(durationMillis = 280, easing = EaseIn),
        label = "pieceAlpha"
    )

    val dropTarget = remember(cell.piece) {
        object : DragAndDropTarget {
            override fun onEntered(event: DragAndDropEvent) { isHovered = true }
            override fun onExited(event: DragAndDropEvent)  { isHovered = false }
            override fun onEnded(event: DragAndDropEvent)   { isHovered = false }
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val trayIndex = event.toAndroidDragEvent()
                    .clipData.getItemAt(0).text.toString()
                    .toIntOrNull() ?: return false
                onDrop(trayIndex)
                return true
            }
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = when {
                    isHovered -> MaterialTheme.colorScheme.primaryContainer
                    else      -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(6.dp)
            )
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    val isEmpty = cell.piece == null
                    isEmpty && event.toAndroidDragEvent()
                        .clipDescription
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dropTarget
            ),
        contentAlignment = Alignment.Center
    ) {
        // La animación está en el pastel, no en la casilla
        if (cell.piece != null || cell.isDisappearing) {
            cell.piece?.let { piece ->
                CakePieceView(
                    piece = piece,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .graphicsLayer {
                            scaleX = pieceScale
                            scaleY = pieceScale
                            alpha  = pieceAlpha
                        }
                )
            }
        }
    }
}