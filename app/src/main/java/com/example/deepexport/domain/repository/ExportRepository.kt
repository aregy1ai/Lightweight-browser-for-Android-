package com.example.deepexport.domain.repository

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.ExportResult
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    suspend fun export(
        conversation: ChatConversation,
        format: ExportFormat,
        fileName: String
    ): AppResult<ExportResult>

    fun getExportHistory(): Flow<List<ExportResult>>
    suspend fun deleteExportHistory(id: Long): AppResult<Unit>
    suspend fun clearHistory(): AppResult<Unit>
}
