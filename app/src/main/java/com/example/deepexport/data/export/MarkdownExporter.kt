package com.example.deepexport.data.export

import com.example.deepexport.core.toFormattedDate
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.MessageRole

class MarkdownExporter : ConversationExportFormatter {
    override fun supports(format: ExportFormat): Boolean = format == ExportFormat.MARKDOWN

    override fun format(conversation: ChatConversation): String = export(conversation)

    fun export(conversation: ChatConversation): String {
        val sb = StringBuilder()
        sb.appendLine("# ${conversation.title}")
        sb.appendLine()
        sb.appendLine("- **المنصة**: ${conversation.platform.displayName}")
        if (conversation.sourceUrl.isNotBlank()) {
            sb.appendLine("- **المصدر**: [${conversation.sourceUrl}](${conversation.sourceUrl})")
        }
        sb.appendLine("- **تاريخ التصدير**: ${conversation.createdAt.toFormattedDate()}")
        sb.appendLine("- **عدد الرسائل**: ${conversation.messages.size}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()

        conversation.messages.forEachIndexed { _, msg ->
            when (msg.role) {
                MessageRole.User -> {
                    sb.appendLine("### 👤 المستخدم")
                }
                MessageRole.Assistant -> {
                    sb.appendLine("### 🤖 ${conversation.platform.displayName}")
                }
                MessageRole.System -> {
                    sb.appendLine("### ⚙️ النظام")
                }
                MessageRole.Unknown -> {
                    sb.appendLine("### 💬 رسالة")
                }
            }
            sb.appendLine()

            if (!msg.thinkingContent.isNullOrBlank()) {
                sb.appendLine("<details>")
                sb.appendLine("<summary>🧠 انقر لعرض سلسلة تفكير النموذج (Thinking Chain)</summary>")
                sb.appendLine()
                sb.appendLine("> ${msg.thinkingContent.replace("\n", "\n> ")}")
                sb.appendLine()
                sb.appendLine("</details>")
                sb.appendLine()
            }

            sb.appendLine(msg.content)
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }

        sb.appendLine("> *تم الاستخراج والتصدير بواسطة AI Chat Exporter*")
        return sb.toString()
    }
}
