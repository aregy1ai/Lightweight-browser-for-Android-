package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

class PerplexityAdapter : PlatformAdapter {
    override val platform: Platform = Platform.PERPLEXITY

    override fun getPrimaryScript(): String = """
        (function() {
            function clean(text) {
                return (text || '').replace(/\u00a0/g, ' ').replace(/\u200b/g, '').trim();
            }

            const title = (document.title || 'Perplexity').replace(/ \| Perplexity.*/, '').trim();
            const url = location.href;

            const queryAnswerPairs = document.querySelectorAll('[class*="Query"], [class*="Answer"], [class*="wrapper"]');
            const messages = [];

            queryAnswerPairs.forEach(el => {
                const isQuery = el.className.toLowerCase().includes('query') || el.querySelector('h1, [class*="question"]') !== null;
                const role = isQuery ? 'User' : 'Assistant';

                const content = clean(el.innerText);
                if (content.length > 5) {
                    messages.push({
                        role: role,
                        content: content,
                        thinking: null,
                        timestamp: Date.now()
                    });
                }
            });

            return JSON.stringify({
                title: title,
                url: url,
                platform: 'PERPLEXITY',
                messages: messages
            });
        })();
    """.trimIndent()

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('main div, article').forEach(el => {
                const s = clean(el.innerText);
                if (s.length > 25 && s.length < 20000) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'Perplexity Search',
                url: location.href,
                platform: 'PERPLEXITY',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.PERPLEXITY)
    }
}
