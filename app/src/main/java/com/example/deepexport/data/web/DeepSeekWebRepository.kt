package com.example.deepexport.data.web

import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import org.json.JSONObject

class DeepSeekWebRepository {

    fun getExtractionScript(): String = ExtractionJs.script

    fun parseResult(rawResult: String, sourceUrl: String): AppResult<ChatConversation> {
        return try {
            // evaluateJavascript returns JSON wrapped in string quotes with escaped chars
            val sanitizedJson = unwrapJsResult(rawResult)
            val root = JSONObject(sanitizedJson)

            val isSuccess = root.optBoolean("success", true)
            if (!isSuccess) {
                val error = root.optString("error", "فشل استخراج المحادثة من DOM")
                return AppResult.Error(IllegalStateException(error))
            }

            val title = root.optString("title", "محادثة DeepSeek").ifBlank { "محادثة DeepSeek" }
            val messagesJson = root.optJSONArray("messages")

            val messagesList = mutableListOf<ChatMessage>()
            if (messagesJson != null) {
                for (i in 0 until messagesJson.length()) {
                    val item = messagesJson.getJSONObject(i)
                    val roleStr = item.optString("role", "assistant")
                    val content = item.optString("content", "")
                    val thinking = item.optString("thinking").takeIf { it.isNotBlank() && it != "null" }
                    val model = item.optString("model").takeIf { it.isNotBlank() && it != "null" }

                    if (content.isNotBlank()) {
                        messagesList.add(
                            ChatMessage(
                                role = MessageRole.fromString(roleStr),
                                content = content,
                                thinkingContent = thinking,
                                model = model
                            )
                        )
                    }
                }
            }

            if (messagesList.isEmpty()) {
                return AppResult.Error(
                    IllegalStateException("لم يتم العثور على رسائل في هذه الصفحة. تأكد من أن المحادثة محملة بالكامل.")
                )
            }

            AppResult.Success(
                ChatConversation(
                    title = title,
                    sourceUrl = sourceUrl,
                    messages = messagesList
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e, "خطأ في تحليل استجابة الجافاسكربت: ${e.localizedMessage}")
        }
    }

    private fun unwrapJsResult(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("\"") && str.endsWith("\"") && str.length >= 2) {
            // It's a quoted JS string with escape sequences, let's unescape
            try {
                str = org.json.JSONTokener(str).nextValue().toString()
            } catch (ignored: Exception) {
                str = str.substring(1, str.length - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\")
            }
        }
        return str
    }
}
