package com.tuapp.mygame.features.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameLogDao {
    @Insert
    suspend fun insert(log: GameLogEntity)

    @Query("SELECT * FROM game_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<GameLogEntity>>
}