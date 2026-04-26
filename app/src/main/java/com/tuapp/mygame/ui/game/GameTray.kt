@file:OptIn(ExperimentalFoundationApi::class)

package com.tuapp.mygame.ui.game

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tuapp.mygame.model.CakePiece
import com.tuapp.mygame.model.CakePieceView
import kotlin.math.cos
import kotlin.math.sin

const val DRAG_LABEL = "cake_piece"

@Composable
fun GameTray(
    pieces: List<CakePiece>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pieces.forEachIndexed { index, piece ->
                TraySlot(piece = piece, trayIndex = index)
            }
        }
    }
}

// Using an experimental api from Jetpack Compose documentation https://developer.android.com/develop/ui/compose/touch-input/user-interactions/drag-and-drop?hl=es-419
@Composable
fun TraySlot(
    piece: CakePiece,
    trayIndex: Int
) {
    val density = LocalDensity.current
    val sizePx = with(density) { 72.dp.toPx() }

    CakePieceView(
        piece = piece,
        modifier = Modifier
            .size(72.dp)
            .dragAndDropSource(
                drawDragDecoration = {
                    val sweep = 90f * piece.slices
                    val radius = sizePx / 2f
                    val center = Offset(radius, radius)

                    drawArc(
                        color = piece.type.color,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = true,
                        size = androidx.compose.ui.geometry.Size(sizePx, sizePx)
                    )
                    drawCircle(
                        color = piece.type.borderColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    repeat(4) { i ->
                        val angle = Math.toRadians((i * 90.0 - 90.0))
                        drawLine(
                            color = piece.type.borderColor,
                            start = center,
                            end = Offset(
                                x = center.x + radius * cos(angle).toFloat(),
                                y = center.y + radius * sin(angle).toFloat()
                            ),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }
            ) {
                detectTapGestures(
                    onPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText(
                                    DRAG_LABEL,
                                    trayIndex.toString()
                                )
                            )
                        )
                    }
                )
            }
    )
}
