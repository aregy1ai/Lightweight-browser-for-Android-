package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.data.web.ExtractionJs
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ChatMessage
import com.example.deepexport.domain.model.MessageRole
import com.example.deepexport.domain.model.Platform
import org.json.JSONObject

class DeepSeekAdapter : PlatformAdapter {
    override val platform: Platform = Platform.DEEPSEEK

    override fun getPrimaryScript(): String = ExtractionJs.script

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('.ds-markdown, [class*="fbb737a4"], [class*="f9bf7997"], main div, article').forEach(el => {
                const s = clean(el.innerText);
                if (s.length > 25 && s.length < 25000) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'DeepSeek Chat',
                url: location.href,
                platform: 'DEEPSEEK',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.DEEPSEEK)
    }
}

fun parseAdapterJson(raw: String, currentUrl: String, platform: Platform): ChatConversation? {
    return try {
        var clean = raw.trim()
        if (clean.startsWith("\"") && clean.endsWith("\"")) {
            clean = clean.substring(1, clean.length - 1)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\\\", "\\")
        }
        val obj = JSONObject(clean)
        val title = obj.optString("title").ifBlank { "${platform.displayName} Chat" }
        val url = obj.optString("url").ifBlank { currentUrl }
        val msgArr = obj.optJSONArray("messages") ?: return null

        val messages = mutableListOf<ChatMessage>()
        for (i in 0 until msgArr.length()) {
            val item = msgArr.getJSONObject(i)
            val role = MessageRole.fromString(item.optString("role", "Assistant"))
            val content = item.optString("content", "")
            val thinking = item.optString("thinking").takeIf { it.isNotBlank() }
            val model = item.optString("model").takeIf { it.isNotBlank() }
            val timestamp = if (item.has("timestamp") && !item.isNull("timestamp")) item.optLong("timestamp") else null

            if (content.isNotBlank() || !thinking.isNullOrBlank()) {
                messages.add(
                    ChatMessage(
                        role = role,
                        content = content,
                        thinkingContent = thinking,
                        model = model,
                        timestamp = timestamp
                    )
                )
            }
        }

        if (messages.isEmpty()) null
        else ChatConversation(
            title = title,
            sourceUrl = url,
            platform = platform,
            messages = messages
        )
    } catch (_: Exception) {
        null
    }
}
