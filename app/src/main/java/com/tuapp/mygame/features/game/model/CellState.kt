package com.tuapp.mygame.features.game.model

data class CellState(
    val row: Int,
    val col: Int,
    val piece: CakePiece? = null,
    val isDisappearing: Boolean = false
)
