package com.example.deepexport.data.extraction

import com.example.deepexport.domain.model.Platform
import org.junit.Assert.assertEquals
import org.junit.Test

class PlatformDetectorTest {

    @Test
    fun `detect from url returns expected platform`() {
        assertEquals(Platform.DEEPSEEK, PlatformDetector.detect("https://chat.deepseek.com/c/123"))
        assertEquals(Platform.CHATGPT, PlatformDetector.detect("https://chatgpt.com/c/456"))
        assertEquals(Platform.CLAUDE, PlatformDetector.detect("https://claude.ai/chat/789"))
        assertEquals(Platform.GEMINI, PlatformDetector.detect("https://gemini.google.com/app"))
        assertEquals(Platform.PERPLEXITY, PlatformDetector.detect("https://www.perplexity.ai/search"))
        assertEquals(Platform.GENERIC, PlatformDetector.detect("https://example.com/unknown"))
    }

    @Test
    fun `detect from pageTitle when url is generic returns platform`() {
        assertEquals(Platform.DEEPSEEK, PlatformDetector.detect("https://myproxy.internal/app", "DeepSeek Conversation"))
        assertEquals(Platform.CHATGPT, PlatformDetector.detect("https://myproxy.internal/app", "ChatGPT - Code Review"))
        assertEquals(Platform.CLAUDE, PlatformDetector.detect("https://myproxy.internal/app", "Claude Assistant Workspace"))
    }
}
