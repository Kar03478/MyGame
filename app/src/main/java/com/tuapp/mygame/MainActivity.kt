package com.tuapp.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuapp.mygame.ui.theme.MyGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGameTheme {
                Game()
            }
        }
    }
}

data class CellState(
    val row: Int,
    val col: Int,
    val piece: CakePiece? = null
)


