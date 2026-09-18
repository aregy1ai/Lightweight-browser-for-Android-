package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat

interface ConversationExportFormatter {
    fun supports(format: ExportFormat): Boolean
    fun format(conversation: ChatConversation): String
}
