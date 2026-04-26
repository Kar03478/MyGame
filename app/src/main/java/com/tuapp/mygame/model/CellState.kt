package com.tuapp.mygame.model

data class CellState(
    val row: Int,
    val col: Int,
    val piece: CakePiece? = null,
    val isDisappearing: Boolean = false
)
