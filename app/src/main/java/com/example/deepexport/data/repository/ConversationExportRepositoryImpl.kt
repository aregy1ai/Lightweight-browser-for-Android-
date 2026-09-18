package com.example.deepexport.data.repository

import com.example.deepexport.data.export.ConversationFileWriter
import com.example.deepexport.data.export.ExportFormatterRegistry
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.repository.ConversationExportRepository
import java.io.File

class ConversationExportRepositoryImpl(
    private val registry: ExportFormatterRegistry,
    private val fileWriter: ConversationFileWriter
) : ConversationExportRepository {

    override fun export(
        conversation: ChatConversation,
        format: ExportFormat,
        directory: File
    ): File {
        val formatter = registry.formatter(format)
        val content = formatter.format(conversation)

        val safeTitle = conversation.title
            .ifBlank { "conversation" }
            .replace(Regex("[^a-zA-Z0-9_\u0600-\u06FF-]"), "_")

        val extension = format.extension
        return fileWriter.writeExport(directory, "$safeTitle.$extension", content)
    }
}
