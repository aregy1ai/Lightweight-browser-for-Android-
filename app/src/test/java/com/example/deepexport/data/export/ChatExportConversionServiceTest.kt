package com.example.deepexport.data.export

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatExportConversionServiceTest {

    private lateinit var service: ChatExportConversionService
    private lateinit var sampleConversation: ChatConversation

    @Before
    fun setUp() {
        service = ChatExportConversionService()
        sampleConversation = ChatConversation(
            id = "conv-123",
            title = "حل مسألة برمجية في Kotlin",
            sourceUrl = "https://chat.deepseek.com/a/chat/s/sample",
            platform = Platform.DEEPSEEK,
            messages = listOf(
                ChatMessage(
                    id = "msg-1",
                    role = MessageRole.User,
                    content = "كيف يمكنني استخدام Room في أندرويد؟"
                ),
                ChatMessage(
                    id = "msg-2",
                    role = MessageRole.Assistant,
                    content = "يمكنك استخدام Room باتباع الخطوات التالية:\n```kotlin\n@Entity\ndata class Item(val id: Int)\n```",
                    thinkingContent = "المستخدم يسأل عن مكتبة Room في نظام أندرويد. يجب توضيح الخطوات الأساسية مع مثال كود."
                )
            )
        )
    }

    @Test
    fun `toTxt returns expected formatted plain text`() {
        val result = service.convert(sampleConversation, ExportFormat.TXT)
        assertTrue(result.contains("عنوان المحادثة: حل مسألة برمجية في Kotlin"))
        assertTrue(result.contains("👤 المستخدم"))
        assertTrue(result.contains("كيف يمكنني استخدام Room في أندرويد؟"))
        assertTrue(result.contains("💭 [سلسلة التفكير - Thinking Process]"))
    }

    @Test
    fun `toMarkdown returns expected markdown with code and thinking details`() {
        val result = service.convert(sampleConversation, ExportFormat.MARKDOWN)
        assertTrue(result.startsWith("# حل مسألة برمجية في Kotlin"))
        assertTrue(result.contains("### 👤 المستخدم"))
        assertTrue(result.contains("### 🤖 DeepSeek"))
        assertTrue(result.contains("<details>"))
        assertTrue(result.contains("```kotlin"))
    }

    @Test
    fun `toJson returns valid json string with messages array`() {
        val result = service.convert(sampleConversation, ExportFormat.JSON)
        assertTrue(result.contains("\"title\": \"حل مسألة برمجية في Kotlin\""))
        assertTrue(result.contains("\"messages\":"))
        assertTrue(result.contains("\"thinkingContent\":"))
    }
}
