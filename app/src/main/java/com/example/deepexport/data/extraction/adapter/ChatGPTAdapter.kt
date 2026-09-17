package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

class ChatGPTAdapter : PlatformAdapter {
    override val platform: Platform = Platform.CHATGPT

    override fun getPrimaryScript(): String = """
        (function() {
            function clean(text) {
                return (text || '').replace(/\u00a0/g, ' ').replace(/\u200b/g, '').trim();
            }

            const title = (document.title || 'ChatGPT').replace(/ \| OpenAI.*/, '').replace(/ - ChatGPT.*/, '').trim();
            const url = location.href;

            const articles = document.querySelectorAll('article');
            const messages = [];

            articles.forEach(art => {
                const roleAttr = art.getAttribute('data-message-author-role') || '';
                const isUser = roleAttr === 'user' || art.querySelector('[data-message-author-role="user"]') !== null;
                const isAssistant = roleAttr === 'assistant' || art.querySelector('[data-message-author-role="assistant"]') !== null;

                const role = isUser ? 'User' : (isAssistant ? 'Assistant' : 'Unknown');

                // Check for thoughts/collapsible details
                const thoughtEl = art.querySelector('[data-message-thought], details');
                const thinking = thoughtEl ? clean(thoughtEl.innerText) : null;

                const textEl = art.querySelector('.markdown, [data-message-id]') || art;
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
                platform: 'CHATGPT',
                messages: messages
            });
        })();
    """.trimIndent()

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('main div[class*="text-base"], [data-message-id]').forEach(el => {
                const s = clean(el.innerText);
                if (s.length > 20) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'ChatGPT',
                url: location.href,
                platform: 'CHATGPT',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.CHATGPT)
    }
}
