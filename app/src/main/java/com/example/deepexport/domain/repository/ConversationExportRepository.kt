package com.example.deepexport.domain.repository

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import java.io.File

interface ConversationExportRepository {
    fun export(
        conversation: ChatConversation,
        format: ExportFormat,
        directory: File
    ): File
}
