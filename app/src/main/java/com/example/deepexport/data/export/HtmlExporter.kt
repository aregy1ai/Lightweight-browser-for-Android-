package com.example.deepexport.data.export

import com.example.deepexport.core.toFormattedDate
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.MessageRole

class HtmlExporter : ConversationExportFormatter {
    override fun supports(format: ExportFormat): Boolean = format == ExportFormat.HTML

    override fun format(conversation: ChatConversation): String = export(conversation)

    fun export(conversation: ChatConversation): String {
        val sb = StringBuilder()
        sb.appendLine("<!DOCTYPE html>")
        sb.appendLine("<html dir=\"auto\" lang=\"ar\">")
        sb.appendLine("<head>")
        sb.appendLine("<meta charset=\"UTF-8\">")
        sb.appendLine("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
        sb.appendLine("<title>${escapeHtml(conversation.title)}</title>")
        sb.appendLine("<style>")
        sb.appendLine("""
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Noto Sans Arabic', Helvetica, Arial, sans-serif;
                background-color: #f8fafc;
                color: #1e293b;
                margin: 0;
                padding: 20px;
                line-height: 1.6;
            }
            .container {
                max-width: 800px;
                margin: 0 auto;
                background: #ffffff;
                padding: 24px;
                border-radius: 16px;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
            }
            .header {
                border-bottom: 2px solid #e2e8f0;
                padding-bottom: 16px;
                margin-bottom: 24px;
            }
            .header h1 {
                margin: 0 0 8px 0;
                font-size: 1.5rem;
                color: #0f172a;
            }
            .meta {
                font-size: 0.875rem;
                color: #64748b;
            }
            .message {
                margin-bottom: 20px;
                padding: 16px;
                border-radius: 12px;
                border: 1px solid #e2e8f0;
            }
            .message.user {
                background-color: #eff6ff;
                border-color: #bfdbfe;
            }
            .message.assistant {
                background-color: #f8fafc;
                border-color: #e2e8f0;
            }
            .message.system {
                background-color: #fef3c7;
                border-color: #fde68a;
            }
            .role {
                font-weight: bold;
                font-size: 0.875rem;
                margin-bottom: 8px;
                display: flex;
                align-items: center;
                gap: 6px;
            }
            .role.user { color: #1d4ed8; }
            .role.assistant { color: #047857; }
            .role.system { color: #b45309; }
            .content {
                white-space: pre-wrap;
                word-break: break-word;
            }
            .thinking {
                background-color: #f1f5f9;
                border-left: 4px solid #94a3b8;
                padding: 10px 14px;
                margin-bottom: 12px;
                border-radius: 6px;
                font-size: 0.9rem;
                color: #475569;
            }
            pre {
                background-color: #1e293b;
                color: #f8fafc;
                padding: 12px;
                border-radius: 8px;
                overflow-x: auto;
            }
            code {
                font-family: monospace;
            }
            footer {
                margin-top: 32px;
                text-align: center;
                font-size: 0.75rem;
                color: #94a3b8;
            }
        """.trimIndent())
        sb.appendLine("</style>")
        sb.appendLine("</head>")
        sb.appendLine("<body>")
        sb.appendLine("<div class=\"container\">")
        sb.appendLine("<div class=\"header\">")
        sb.appendLine("<h1>${escapeHtml(conversation.title)}</h1>")
        sb.appendLine("<div class=\"meta\">")
        sb.appendLine("<div>المنصة: <strong>${escapeHtml(conversation.platform.displayName)}</strong></div>")
        if (conversation.sourceUrl.isNotBlank()) {
            sb.appendLine("<div>المصدر: <a href=\"${escapeHtml(conversation.sourceUrl)}\" target=\"_blank\">${escapeHtml(conversation.sourceUrl)}</a></div>")
        }
        sb.appendLine("<div>تاريخ التصدير: ${conversation.createdAt.toFormattedDate()} | عدد الرسائل: ${conversation.messages.size}</div>")
        sb.appendLine("</div>")
        sb.appendLine("</div>")

        conversation.messages.forEach { msg ->
            val roleClass = when (msg.role) {
                MessageRole.User -> "user"
                MessageRole.Assistant -> "assistant"
                MessageRole.System -> "system"
                MessageRole.Unknown -> "assistant"
            }
            val roleLabel = when (msg.role) {
                MessageRole.User -> "👤 المستخدم"
                MessageRole.Assistant -> "🤖 ${conversation.platform.displayName}"
                MessageRole.System -> "⚙️ النظام"
                MessageRole.Unknown -> "💬 رسالة"
            }

            sb.appendLine("<div class=\"message $roleClass\">")
            sb.appendLine("<div class=\"role $roleClass\">$roleLabel</div>")

            if (!msg.thinkingContent.isNullOrBlank()) {
                sb.appendLine("<details class=\"thinking\">")
                sb.appendLine("<summary>🧠 سلسلة تفكير النموذج</summary>")
                sb.appendLine("<div style=\"margin-top:8px;\">${escapeHtml(msg.thinkingContent)}</div>")
                sb.appendLine("</details>")
            }

            sb.appendLine("<div class=\"content\">${escapeHtml(msg.content)}</div>")
            sb.appendLine("</div>")
        }

        sb.appendLine("<footer>تم التصدير بواسطة تطبيق AI Chat Exporter</footer>")
        sb.appendLine("</div>")
        sb.appendLine("</body>")
        sb.appendLine("</html>")

        return sb.toString()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
