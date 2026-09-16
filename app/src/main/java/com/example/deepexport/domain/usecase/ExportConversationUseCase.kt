package com.example.deepexport.domain.usecase

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.ExportResult
import com.example.deepexport.domain.repository.ExportRepository

class ExportConversationUseCase(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(
        conversation: ChatConversation,
        format: ExportFormat,
        fileName: String
    ): AppResult<ExportResult> {
        return exportRepository.export(conversation, format, fileName)
    }
}
