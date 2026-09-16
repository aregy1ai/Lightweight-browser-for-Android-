package com.example.deepexport.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "export_history")
data class ExportHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val fileName: String,
    val filePath: String,
    val format: String,
    val messageCount: Int,
    val exportedAt: Long = System.currentTimeMillis(),
    val status: String = "نجح"
)
