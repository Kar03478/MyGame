package com.tuapp.mygame.features.game.model

data class GameLog (
    val alias: String,
    val score: Int,
    val gridSize: Int,
    val durationSeconds: Long,
    val endReason: EndReason
)

enum class EndReason { BOARD_FULL, TIME_UP, WIN }
