package com.example.deepexport.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val contentBlocks: List<ContentBlock> = emptyList(),
    val thinkingContent: String? = null,
    val model: String? = null,
    val timestamp: Long? = null
)
