package com.example.deepexport.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deepexport.data.local.entity.ExportHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExportHistoryDao {
    @Query("SELECT * FROM export_history ORDER BY exportedAt DESC")
    fun getAllHistory(): Flow<List<ExportHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ExportHistoryEntity): Long

    @Query("DELETE FROM export_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM export_history")
    suspend fun clearAll()
}
