package com.tuapp.mygame.features.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_logs")
data class GameLogEntity(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val alias: String,
    val score: Int,
    val rows: Int,
    val cols: Int,
    val durationSeconds: Long,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
)
