package com.example.deepexport

import com.example.deepexport.data.export.JsonExporter
import com.example.deepexport.data.export.MarkdownExporter
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import org.junit.Assert.assertTrue
import org.junit.Test

class ExporterTest {

    private val sampleConversation = ChatConversation(
        title = "تحليل خوارزمية البحث الثنائي",
        sourceUrl = "https://chat.deepseek.com/chat/123",
        messages = listOf(
            ChatMessage(
                role = MessageRole.User,
                content = "اشرح لي البحث الثنائي في لغة كوتلن مع مثال."
            ),
            ChatMessage(
                role = MessageRole.Assistant,
                content = "البحث الثنائي خوارزمية سريعة تعتمد على تقسيم المصفوفة المرتبة:\n```kotlin\nfun binarySearch() {}\n```",
                thinkingContent = "المستخدم يسأل عن Binary Search باللغة العربية وفي لغة Kotlin."
            )
        )
    )

    @Test
    fun `markdown exporter includes title, messages, and thinking process`() {
        val exporter = MarkdownExporter()
        val markdown = exporter.export(sampleConversation)

        assertTrue(markdown.contains("# تحليل خوارزمية البحث الثنائي"))
        assertTrue(markdown.contains("اشرح لي البحث الثنائي"))
        assertTrue(markdown.contains("سلسلة التفكير"))
        assertTrue(markdown.contains("Binary Search"))
        assertTrue(markdown.contains("```kotlin"))
    }

    @Test
    fun `txt exporter exports clear readable plain text`() {
        val exporter = TxtExporter()
        val txt = exporter.export(sampleConversation)

        assertTrue(txt.contains("تحليل خوارزمية البحث الثنائي"))
        assertTrue(txt.contains("[المستخدم]"))
        assertTrue(txt.contains("[DeepSeek AI]"))
    }

    @Test
    fun `json exporter produces valid structured json`() {
        val exporter = JsonExporter()
        val json = exporter.export(sampleConversation)

        assertTrue(json.contains("\"title\": \"تحليل خوارزمية البحث الثنائي\""))
        assertTrue(json.contains("\"messages\""))
        assertTrue(json.contains("\"role\": \"User\""))
        assertTrue(json.contains("\"role\": \"Assistant\""))
    }
}
