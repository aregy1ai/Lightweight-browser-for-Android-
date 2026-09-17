package com.example.deepexport.domain.model

import java.util.UUID

data class ChatConversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val sourceUrl: String,
    val platform: Platform = Platform.fromUrl(sourceUrl),
    val messages: List<ChatMessage>,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalMessageCount: Int get() = messages.size
    val totalCharacters: Int get() = messages.sumOf {
        it.content.length + (it.thinkingContent?.length ?: 0)
    }
}
