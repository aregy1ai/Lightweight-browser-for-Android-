package com.example.deepexport.data.extraction.pipeline

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.ContentBlock
import com.example.deepexport.domain.model.MessageRole

object TextProcessingPipeline {

    fun normalizeText(input: String): String {
        return input.replace("\u00a0", " ")
            .replace("\u200b", "")
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }

    fun parseContentBlocks(content: String, thinking: String?): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()

        if (!thinking.isNullOrBlank()) {
            blocks.add(ContentBlock.Thinking(normalizeText(thinking)))
        }

        val codeRegex = Regex("```([a-zA-Z0-9_-]*)\\s*\\n?([\\s\\S]*?)```")
        var lastIndex = 0

        for (match in codeRegex.findAll(content)) {
            val range = match.range
            if (range.first > lastIndex) {
                val textPart = content.substring(lastIndex, range.first).trim()
                if (textPart.isNotBlank()) {
                    blocks.add(ContentBlock.Text(textPart))
                }
            }
            val lang = match.groupValues[1].trim().takeIf { it.isNotBlank() }
            val code = match.groupValues[2].trimEnd()
            blocks.add(ContentBlock.Code(code = code, language = lang))
            lastIndex = range.last + 1
        }

        if (lastIndex < content.length) {
            val remaining = content.substring(lastIndex).trim()
            if (remaining.isNotBlank()) {
                blocks.add(ContentBlock.Text(remaining))
            }
        }

        if (blocks.isEmpty() && content.isNotBlank()) {
            blocks.add(ContentBlock.Text(content))
        }

        return blocks
    }

    fun cleanAndDeduplicateMessages(messages: List<ChatMessage>): List<ChatMessage> {
        val cleanedList = mutableListOf<ChatMessage>()

        for (msg in messages) {
            val cleanContent = normalizeText(msg.content)
            val cleanThinking = msg.thinkingContent?.let { normalizeText(it) }

            if (cleanContent.isBlank() && cleanThinking.isNullOrBlank()) {
                continue
            }

            val blocks = parseContentBlocks(cleanContent, cleanThinking)

            // Deduplicate exact adjacent identical messages from lazy scrolling
            val last = cleanedList.lastOrNull()
            if (last != null && last.role == msg.role && last.content == cleanContent && last.thinkingContent == cleanThinking) {
                continue
            }

            cleanedList.add(
                msg.copy(
                    content = cleanContent,
                    thinkingContent = cleanThinking,
                    contentBlocks = blocks
                )
            )
        }

        return cleanedList
    }

    fun process(conversation: ChatConversation): ChatConversation {
        val cleanTitle = normalizeText(conversation.title).ifBlank { "${conversation.platform.displayName} Chat" }
        val processedMessages = cleanAndDeduplicateMessages(conversation.messages)
        return conversation.copy(
            title = cleanTitle,
            messages = processedMessages
        )
    }
}
