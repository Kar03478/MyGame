@file:OptIn(ExperimentalFoundationApi::class)

package com.tuapp.mygame

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.unit.dp

const val DRAG_LABEL = "cake_piece"

@Composable
fun GameTray(
    pieces: List<CakePiece>,
) {
    Card(Modifier.fillMaxWidth()) {
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
    CakePieceView(
        piece = piece,
        modifier = Modifier
            .size(72.dp)
            .dragAndDropSource(
                drawDragDecoration = {}
            ) {
                detectTapGestures(
                    onLongPress = {
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