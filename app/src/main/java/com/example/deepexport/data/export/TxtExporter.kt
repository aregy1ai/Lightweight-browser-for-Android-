package com.example.deepexport.data.export

import com.example.deepexport.core.toFormattedDate
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.MessageRole

class TxtExporter {
    fun export(conversation: ChatConversation): String {
        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("عنوان المحادثة: ${conversation.title}")
        sb.appendLine("المنصة: ${conversation.platform.displayName}")
        sb.appendLine("المصدر: ${conversation.sourceUrl}")
        sb.appendLine("تاريخ التصدير: ${conversation.createdAt.toFormattedDate()}")
        sb.appendLine("عدد الرسائل: ${conversation.messages.size}")
        sb.appendLine("==================================================")
        sb.appendLine()

        conversation.messages.forEachIndexed { index, msg ->
            val sender = when (msg.role) {
                MessageRole.User -> "👤 المستخدم (User)"
                MessageRole.Assistant -> "🤖 ${conversation.platform.displayName} (Assistant)"
                MessageRole.System -> "⚙️ النظام (System)"
                MessageRole.Unknown -> "❓ رسالة (${msg.role.name})"
            }
            sb.appendLine("--- [$sender] (${index + 1}/${conversation.messages.size}) ---")

            if (!msg.thinkingContent.isNullOrBlank()) {
                sb.appendLine("💭 [سلسلة التفكير - Thinking Process]:")
                sb.appendLine(msg.thinkingContent)
                sb.appendLine()
            }

            sb.appendLine(msg.content)
            sb.appendLine()
        }

        sb.appendLine("==================================================")
        sb.appendLine("تم التصدير بواسطة تطبيق AI Chat Exporter")
        return sb.toString()
    }
}
