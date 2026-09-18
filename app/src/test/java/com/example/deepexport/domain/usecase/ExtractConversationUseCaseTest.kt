package com.example.deepexport.domain.usecase

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExtractConversationUseCaseTest {

    private val useCase = ExtractConversationUseCase()

    @Test
    fun `invoke with blank input returns Error`() {
        val result = useCase("", "https://chat.deepseek.com")
        assertTrue(result is AppResult.Error)
    }

    @Test
    fun `invoke with valid json extracts conversation and messages correctly`() {
        val sampleJson = """
            {
                "title": "محادثة عميقة",
                "url": "https://chat.deepseek.com/chat/100",
                "messages": [
                    {
                        "role": "User",
                        "content": "كيف أكتب كود Kotlin نظيف؟",
                        "timestamp": 1700000000000
                    },
                    {
                        "role": "Assistant",
                        "content": "استخدم النماذج غير القابلة للتغيير وعزل الطبقات.",
                        "thinking": "المستخدم يسأل عن مبادئ كتابة الكود النظيف في كوتلن.",
                        "timestamp": 1700000005000
                    }
                ]
            }
        """.trimIndent()

        val result = useCase(sampleJson, "https://chat.deepseek.com/chat/100")
        assertTrue(result is AppResult.Success)

        val conv = (result as AppResult.Success).data
        assertEquals("محادثة عميقة", conv.title)
        assertEquals(Platform.DEEPSEEK, conv.platform)
        assertEquals(2, conv.messages.size)
        assertEquals(MessageRole.User, conv.messages[0].role)
        assertEquals(MessageRole.Assistant, conv.messages[1].role)
        assertEquals("المستخدم يسأل عن مبادئ كتابة الكود النظيف في كوتلن.", conv.messages[1].thinkingContent)
    }

    @Test
    fun `invoke with empty messages array returns Error`() {
        val emptyJson = """
            {
                "title": "محادثة فارغة",
                "url": "https://chat.deepseek.com",
                "messages": []
            }
        """.trimIndent()

        val result = useCase(emptyJson, "https://chat.deepseek.com")
        assertTrue(result is AppResult.Error)
    }
}
