package com.example.deepexport.domain.usecase

import com.example.deepexport.core.AppResult
import com.example.deepexport.data.extraction.PlatformDetector
import com.example.deepexport.data.extraction.pipeline.TextProcessingPipeline
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import org.json.JSONObject

class ExtractConversationUseCase {

    operator fun invoke(rawJsonOrText: String, currentUrl: String): AppResult<ChatConversation> {
        return try {
            if (rawJsonOrText.isBlank() || rawJsonOrText == "null" || rawJsonOrText == "{}" || rawJsonOrText == "\"\"") {
                return AppResult.Error(
                    IllegalArgumentException("لم يتم العثور على محادثة مستخرجة. تأكد من تحميل الصفحة واحتوائها على رسائل.")
                )
            }

            // Clean JS returned string if it's JSON stringified
            var cleanInput = rawJsonOrText.trim()
            if (cleanInput.startsWith("\"") && cleanInput.endsWith("\"")) {
                cleanInput = cleanInput.substring(1, cleanInput.length - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\\\", "\\")
            }

            val json = JSONObject(cleanInput)
            val platform = PlatformDetector.detect(currentUrl, json.optString("title"))
            val title = json.optString("title").ifBlank { "${platform.displayName} Chat" }
            val url = json.optString("url").ifBlank { currentUrl }
            val messagesArray = json.optJSONArray("messages")

            val messages = mutableListOf<ChatMessage>()
            if (messagesArray != null && messagesArray.length() > 0) {
                for (i in 0 until messagesArray.length()) {
                    val msgObj = messagesArray.getJSONObject(i)
                    val role = MessageRole.fromString(msgObj.optString("role", "Assistant"))
                    val content = msgObj.optString("content").trim()
                    val thinking = msgObj.optString("thinking").takeIf { it.isNotBlank() }
                    val timestamp = if (msgObj.has("timestamp")) msgObj.optLong("timestamp") else null

                    if (content.isNotBlank() || !thinking.isNullOrBlank()) {
                        messages.add(
                            ChatMessage(
                                role = role,
                                content = content,
                                thinkingContent = thinking,
                                timestamp = timestamp
                            )
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                return AppResult.Error(
                    IllegalStateException("لم يتم التعرف على أي رسائل في هذه الصفحة. يرجى التأكد من تسجيل الدخول وفتح المحادثة.")
                )
            }

            val rawConversation = ChatConversation(
                title = title,
                sourceUrl = url,
                platform = platform,
                messages = messages
            )

            val processed = TextProcessingPipeline.process(rawConversation)
            AppResult.Success(processed)
        } catch (e: Exception) {
            AppResult.Error(e, "فشل في معالجة بيانات المحادثة: ${e.localizedMessage}")
        }
    }
}
