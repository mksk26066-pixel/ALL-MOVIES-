package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: WatchHistoryEntity)

    @Query("SELECT * FROM watch_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryForUser(userId: Int): Flow<List<WatchHistoryEntity>>
    
    @Query("SELECT * FROM watch_history WHERE userId = :userId AND videoId = :videoId LIMIT 1")
    suspend fun getHistoryForVideo(userId: Int, videoId: Int): WatchHistoryEntity?
}
