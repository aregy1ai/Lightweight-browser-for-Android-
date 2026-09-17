package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

class GeminiAdapter : PlatformAdapter {
    override val platform: Platform = Platform.GEMINI

    override fun getPrimaryScript(): String = """
        (function() {
            function clean(text) {
                return (text || '').replace(/\u00a0/g, ' ').replace(/\u200b/g, '').trim();
            }

            const title = (document.title || 'Gemini').replace(/ - Google Gemini.*/, '').replace(/Gemini - /, '').trim();
            const url = location.href;

            const turns = document.querySelectorAll('user-query, model-response, [class*="query-container"], [class*="response-container"], message-content');
            const messages = [];

            turns.forEach(turn => {
                const tag = turn.tagName.toLowerCase();
                const isUser = tag.includes('user') || turn.className.includes('query');
                const role = isUser ? 'User' : 'Assistant';

                // Thoughts / drafts
                const thoughtEl = turn.querySelector('.thought-content, [class*="thought"], details');
                const thinking = thoughtEl ? clean(thoughtEl.innerText) : null;

                const textEl = turn.querySelector('.markdown, .message-content, [class*="text"]') || turn;
                const content = clean(textEl.innerText);

                if (content || thinking) {
                    messages.push({
                        role: role,
                        content: content,
                        thinking: thinking,
                        timestamp: Date.now()
                    });
                }
            });

            return JSON.stringify({
                title: title,
                url: url,
                platform: 'GEMINI',
                messages: messages
            });
        })();
    """.trimIndent()

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('main .content, main p').forEach(el => {
                const s = clean(el.innerText);
                if (s.length > 20) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'Gemini Chat',
                url: location.href,
                platform: 'GEMINI',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.GEMINI)
    }
}
