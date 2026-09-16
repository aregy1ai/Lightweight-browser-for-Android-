package com.example.deepexport.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "export_history")
data class ExportRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationTitle: String,
    val sourceUrl: String,
    val format: String,
    val fileName: String,
    val filePath: String,
    val messageCount: Int,
    val characterCount: Int,
    val exportedAt: Long,
    val success: Boolean,
    val errorMessage: String? = null
)
