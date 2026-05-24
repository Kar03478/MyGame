package com.tuapp.mygame.features.game.model

data class GameEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val message: String
)
