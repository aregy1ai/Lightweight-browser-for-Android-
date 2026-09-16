package com.example.deepexport.domain.model

data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val thinkingContent: String? = null,
    val model: String? = null,
    val timestamp: Long? = null
)
