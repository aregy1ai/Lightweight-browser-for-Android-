package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

class GenericWebAdapter : PlatformAdapter {
    override val platform: Platform = Platform.GENERIC

    override fun getPrimaryScript(): String = """
        (function() {
            function clean(text) {
                return (text || '').replace(/\u00a0/g, ' ').replace(/\u200b/g, '').trim();
            }

            const title = document.title || 'محادثة ويب مستخرجة';
            const url = location.href;

            const candidates = document.querySelectorAll('article, [role="main"] > div, .chat-message, [class*="message"], [class*="chat"]');
            const messages = [];

            candidates.forEach((el, index) => {
                const text = clean(el.innerText);
                if (text.length > 15 && text.length < 35000) {
                    const isUser = el.className.toLowerCase().includes('user') ||
                                   el.innerText.toLowerCase().startsWith('user:');
                    messages.push({
                        role: isUser ? 'User' : (index % 2 === 0 ? 'User' : 'Assistant'),
                        content: text,
                        thinking: null,
                        timestamp: Date.now()
                    });
                }
            });

            return JSON.stringify({
                title: title,
                url: url,
                platform: 'GENERIC',
                messages: messages
            });
        })();
    """.trimIndent()

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('main p, article p, [role="main"] p').forEach(p => {
                const s = clean(p.innerText);
                if (s.length > 20) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'Web Conversation',
                url: location.href,
                platform: 'GENERIC',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.GENERIC)
    }
}
