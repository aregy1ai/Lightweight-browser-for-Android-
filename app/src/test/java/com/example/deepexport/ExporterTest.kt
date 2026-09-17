package com.example.deepexport

import com.example.deepexport.data.export.HtmlExporter
import com.example.deepexport.data.export.JsonExporter
import com.example.deepexport.data.export.MarkdownExporter
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.data.extraction.PlatformDetector
import com.example.deepexport.data.extraction.pipeline.TextProcessingPipeline
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExporterTest {

    private val sampleConversation = ChatConversation(
        title = "تحليل خوارزمية البحث الثنائي",
        sourceUrl = "https://chat.deepseek.com/chat/123",
        platform = Platform.DEEPSEEK,
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
        assertTrue(markdown.contains("سلسلة تفكير"))
        assertTrue(markdown.contains("Binary Search"))
        assertTrue(markdown.contains("```kotlin"))
    }

    @Test
    fun `txt exporter exports clear readable plain text`() {
        val exporter = TxtExporter()
        val txt = exporter.export(sampleConversation)

        assertTrue(txt.contains("تحليل خوارزمية البحث الثنائي"))
        assertTrue(txt.contains("المستخدم"))
        assertTrue(txt.contains("DeepSeek"))
    }

    @Test
    fun `json exporter produces valid structured json`() {
        val exporter = JsonExporter()
        val json = exporter.export(sampleConversation)

        assertTrue(json.contains("\"title\": \"تحليل خوارزمية البحث الثنائي\""))
        assertTrue(json.contains("\"messages\""))
        assertTrue(json.contains("\"role\": \"User\""))
        assertTrue(json.contains("\"role\": \"Assistant\""))
        assertTrue(json.contains("\"platform\": \"DEEPSEEK\""))
    }

    @Test
    fun `html exporter produces responsive styled html document`() {
        val exporter = HtmlExporter()
        val html = exporter.export(sampleConversation)

        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("تحليل خوارزمية البحث الثنائي"))
        assertTrue(html.contains("DeepSeek"))
        assertTrue(html.contains("سلسلة تفكير النموذج"))
    }

    @Test
    fun `platform detector correctly identifies platforms from url`() {
        assertEquals(Platform.DEEPSEEK, PlatformDetector.detectFromUrl("https://chat.deepseek.com/c/123"))
        assertEquals(Platform.CHATGPT, PlatformDetector.detectFromUrl("https://chatgpt.com/c/abc"))
        assertEquals(Platform.CLAUDE, PlatformDetector.detectFromUrl("https://claude.ai/chat/xyz"))
        assertEquals(Platform.GEMINI, PlatformDetector.detectFromUrl("https://gemini.google.com/app"))
        assertEquals(Platform.PERPLEXITY, PlatformDetector.detectFromUrl("https://www.perplexity.ai/search/test"))
        assertEquals(Platform.GENERIC, PlatformDetector.detectFromUrl("https://example.com/chat"))
    }

    @Test
    fun `text processing pipeline extracts code blocks properly`() {
        val raw = "Here is Kotlin code:\n```kotlin\nval x = 10\n```\nDone."
        val blocks = TextProcessingPipeline.parseContentBlocks(raw, null)

        assertEquals(3, blocks.size)
        assertTrue(blocks[1] is com.example.deepexport.domain.model.ContentBlock.Code)
    }
}
