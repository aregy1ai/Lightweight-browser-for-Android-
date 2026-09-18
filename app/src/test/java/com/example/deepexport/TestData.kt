package com.example.deepexport

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform

object TestData {
    val conversation = ChatConversation(
        id = "test-123",
        title = "Sample Chat",
        sourceUrl = "https://example.com/chat/123",
        platform = Platform.DEEPSEEK,
        createdAt = 1_700_000_000_000L,
        messages = listOf(
            ChatMessage(
                id = "msg-1",
                role = MessageRole.User,
                content = "Hello, how do I use deep export?",
                timestamp = 1_700_000_000_100L
            ),
            ChatMessage(
                id = "msg-2",
                role = MessageRole.Assistant,
                content = "You can export in TXT, Markdown, JSON, and HTML formats easily!",
                thinkingContent = "User needs assistance on export options.",
                timestamp = 1_700_000_000_200L
            )
        )
    )
}
