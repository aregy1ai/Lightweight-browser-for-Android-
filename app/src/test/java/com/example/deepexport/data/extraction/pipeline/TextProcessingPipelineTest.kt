package com.example.deepexport.data.extraction.pipeline

import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.ContentBlock
import com.example.deepexport.domain.model.MessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextProcessingPipelineTest {

    @Test
    fun `normalizeText removes zero-width spaces and collapses whitespaces`() {
        val raw = "Hello\u200b   world!\u00a0\nHow are   you?"
        val normalized = TextProcessingPipeline.normalizeText(raw)
        val expected = "Hello world!\nHow are you?"
        assertEquals(expected, normalized)
    }

    @Test
    fun `parseContentBlocks separates text and code blocks`() {
        val raw = """
            Here is the code:
            ```kotlin
            fun test() = 42
            ```
            Hope this helps!
        """.trimIndent()

        val blocks = TextProcessingPipeline.parseContentBlocks(raw, "Thinking about Kotlin solution")

        assertEquals(4, blocks.size)
        assertTrue(blocks[0] is ContentBlock.Thinking)
        assertTrue(blocks[1] is ContentBlock.Text)
        assertTrue(blocks[2] is ContentBlock.Code)
        assertTrue(blocks[3] is ContentBlock.Text)

        val codeBlock = blocks[2] as ContentBlock.Code
        assertEquals("kotlin", codeBlock.language)
        assertEquals("fun test() = 42", codeBlock.code)
    }

    @Test
    fun `cleanAndDeduplicateMessages removes adjacent exact duplicates`() {
        val list = listOf(
            ChatMessage(role = MessageRole.User, content = "Hello"),
            ChatMessage(role = MessageRole.User, content = "Hello"),
            ChatMessage(role = MessageRole.Assistant, content = "Hi there!")
        )

        val cleaned = TextProcessingPipeline.cleanAndDeduplicateMessages(list)
        assertEquals(2, cleaned.size)
        assertEquals(MessageRole.User, cleaned[0].role)
        assertEquals(MessageRole.Assistant, cleaned[1].role)
    }
}
