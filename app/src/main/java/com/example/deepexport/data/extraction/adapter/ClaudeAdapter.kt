package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

class ClaudeAdapter : PlatformAdapter {
    override val platform: Platform = Platform.CLAUDE

    override fun getPrimaryScript(): String = """
        (function() {
            function clean(text) {
                return (text || '').replace(/\u00a0/g, ' ').replace(/\u200b/g, '').trim();
            }

            const title = (document.title || 'Claude').replace(/ - Claude.*/, '').replace(/Claude - /, '').trim();
            const url = location.href;

            const messageEls = document.querySelectorAll('[data-test-render-count], .font-claude-message, [class*="HumanMessage"], [class*="AssistantMessage"]');
            const messages = [];

            messageEls.forEach(el => {
                const isHuman = el.className.includes('Human') || el.closest('[class*="Human"]') !== null;
                const role = isHuman ? 'User' : 'Assistant';

                // Thinking / reasoning block in Claude 3.7
                const thinkingEl = el.querySelector('[class*="thought"], [class*="thinking"], details');
                const thinking = thinkingEl ? clean(thinkingEl.innerText) : null;

                const content = clean(el.innerText);
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
                platform: 'CLAUDE',
                messages: messages
            });
        })();
    """.trimIndent()

    override fun getFallbackScript(): String = """
        (function() {
            function clean(t){ return (t || '').replace(/\u00a0/g,' ').trim(); }
            const texts = [];
            document.querySelectorAll('main div.grid, main [class*="message"]').forEach(el => {
                const s = clean(el.innerText);
                if (s.length > 20) texts.push(s);
            });
            const unique = [...new Set(texts)];
            return JSON.stringify({
                title: document.title || 'Claude Chat',
                url: location.href,
                platform: 'CLAUDE',
                messages: unique.map((c, i) => ({
                    role: i % 2 === 0 ? 'User' : 'Assistant',
                    content: c,
                    thinking: null
                }))
            });
        })();
    """.trimIndent()

    override fun parseJson(raw: String, currentUrl: String): ChatConversation? {
        return parseAdapterJson(raw, currentUrl, Platform.CLAUDE)
    }
}
