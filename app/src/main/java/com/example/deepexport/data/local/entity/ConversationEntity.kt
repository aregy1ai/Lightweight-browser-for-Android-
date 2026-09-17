package com.example.deepexport.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceUrl: String,
    val platform: String,
    val messageCount: Int,
    val rawJsonMessages: String,
    val createdAt: Long,
    val updatedAt: Long = createdAt
)
