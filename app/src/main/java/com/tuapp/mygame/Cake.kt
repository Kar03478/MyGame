package com.tuapp.mygame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class CakeType(val color: Color, val borderColor: Color) {
    STRAWBERRY(Color(0xFFF09595), Color(0xFFE24B4A)),
    LEMON(Color(0xFFFAC775), Color(0xFFBA7517)),
    BLUEBERRY(Color(0xFFCECBF6), Color(0xFF534AB7))
}
val TOTAL_PLATE_SLICES = 4
data class CakePiece(
    val type: CakeType,
    val slices: Int // 1, 2, 3, 4
)

@Composable
fun CakePieceView(piece: CakePiece, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val sweep = 90f * piece.slices
        drawArc(
            color = piece.type.color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = true
        )
        drawCircle(
            color = piece.type.borderColor,
            style = Stroke(width = 2.dp.toPx())
        )
        repeat(TOTAL_PLATE_SLICES) { i ->
            val angle = Math.toRadians((i * 90.0 - 90.0))
            drawLine(
                color = piece.type.borderColor,
                start = center,
                end = Offset(
                    x = center.x + (size.minDimension / 2) * cos(angle).toFloat(),
                    y = center.y + (size.minDimension / 2) * sin(angle).toFloat()
                ),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}