package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat

/**
 * Service to convert [ChatConversation] objects into TXT, Markdown, or JSON string formats.
 */
class ChatExportConversionService(
    private val txtExporter: TxtExporter = TxtExporter(),
    private val markdownExporter: MarkdownExporter = MarkdownExporter(),
    private val jsonExporter: JsonExporter = JsonExporter()
) {

    /**
     * Converts a given [ChatConversation] into the requested format (TXT, Markdown, or JSON).
     *
     * @param conversation The chat session object to convert.
     * @param format The target export format (TXT, MARKDOWN, JSON).
     * @return Formatted string representation of the conversation.
     */
    fun convert(conversation: ChatConversation, format: ExportFormat): String {
        return when (format) {
            ExportFormat.TXT -> toTxt(conversation)
            ExportFormat.MARKDOWN -> toMarkdown(conversation)
            ExportFormat.JSON -> toJson(conversation)
            ExportFormat.HTML -> HtmlExporter().export(conversation)
        }
    }

    /**
     * Converts conversation to a clean, human-readable plain text format (TXT).
     */
    fun toTxt(conversation: ChatConversation): String {
        return txtExporter.export(conversation)
    }

    /**
     * Converts conversation to GitHub-flavored Markdown format with collapsible reasoning chains and syntax-highlighted code blocks.
     */
    fun toMarkdown(conversation: ChatConversation): String {
        return markdownExporter.export(conversation)
    }

    /**
     * Converts conversation to structured, formatted JSON with full metadata and message list.
     */
    fun toJson(conversation: ChatConversation): String {
        return jsonExporter.export(conversation)
    }
}
