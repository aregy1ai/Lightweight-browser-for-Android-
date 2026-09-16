package com.example.deepexport.domain.model

data class ChatConversation(
    val title: String,
    val sourceUrl: String,
    val messages: List<ChatMessage>,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalMessageCount: Int get() = messages.size
    val totalCharacters: Int get() = messages.sumOf { it.content.length + (it.thinkingContent?.length ?: 0) }
}
