package com.example.deepexport.data.web

object ExtractionJs {

    /**
     * JavaScript code evaluated inside DeepSeek WebView.
     * Specifically targets DeepSeek's chat bubble class names, data-attributes,
     * and DOM hierarchy for high-fidelity parsing of messages, code, and thinking chains.
     */
    val script: String = """
        (function() {
            try {
                function cleanText(text) {
                    return (text || '')
                        .replace(/\u00a0/g, ' ')
                        .replace(/\u200b/g, '')
                        .trim();
                }

                // 1. Detect Conversation Title
                let pageTitle = document.title || 'DeepSeek Chat';
                const titleCandidate = document.querySelector(
                    '[class*="session-item-selected"] [class*="title"], [class*="chat-session-title"], [data-testid="chat-title"], header [class*="title"], header h1'
                );
                if (titleCandidate && titleCandidate.innerText.trim()) {
                    pageTitle = titleCandidate.innerText.trim();
                } else {
                    pageTitle = pageTitle.replace(/ - DeepSeek.*$/, '').replace(/DeepSeek\s*[-|–]\s*/i, '').trim();
                }

                // 2. Helper to extract formatted text while preserving code blocks with languages
                function extractFormattedNode(node) {
                    if (!node) return '';
                    const clone = node.cloneNode(true);

                    // Remove UI clutter (copy buttons, feedback icons, action toolbars)
                    clone.querySelectorAll(
                        'button, .ds-icon-button, [class*="copy"], [class*="feedback"], [class*="action"], svg, [aria-hidden="true"]'
                    ).forEach(el => el.remove());

                    // Replace code blocks with proper Markdown fences
                    clone.querySelectorAll('pre').forEach(pre => {
                        const codeElem = pre.querySelector('code') || pre;
                        const langMatch = (codeElem.className || '').match(/language-([a-zA-Z0-9#+_-]+)/) ||
                                          (pre.className || '').match(/language-([a-zA-Z0-9#+_-]+)/);
                        const langAttr = codeElem.getAttribute('data-lang') || pre.getAttribute('data-lang') || '';
                        const lang = langMatch ? langMatch[1] : langAttr;
                        const codeText = codeElem.innerText || codeElem.textContent || '';
                        const fenced = document.createTextNode('\n```' + lang + '\n' + codeText.trim() + '\n```\n');
                        pre.parentNode.replaceChild(fenced, pre);
                    });

                    return cleanText(clone.innerText || clone.textContent || '');
                }

                const extractedMessages = [];
                const seenContents = new Set();

                // 3. Specific DeepSeek Chat Bubble Selectors:
                // - User bubbles: .f9bf7997, [class*="f9bf7997"], div[class*="fa81"], .chat-message-user, [data-role="user"], .ds-markdown--user, [class*="dad65929"]
                // - Assistant bubbles: .ds-markdown, div[class*="fbb737a4"], div.fbb737a4, [data-role="assistant"], .chat-message-assistant, .ds-message
                // - Thinking blocks (DeepSeek R1): .ds-think, .ds-think-content, div[class*="f6d670"], div.f6d670, details, [class*="reasoning"]
                const bubbleSelectors = [
                    'div[data-testid="chat-message"]',
                    'div[data-role="user"]',
                    'div[data-role="assistant"]',
                    'div[class*="chat-message"]',
                    'div[class*="chat-item"]',
                    'div[class*="fbb737a4"]',
                    'div[class*="f9bf7997"]',
                    'div[class*="fa81"]',
                    'div[class*="dad65929"]',
                    '.ds-message',
                    '.ds-chat-turn',
                    'div[class*="ds-markdown"]'
                ].join(', ');

                const foundBubbles = document.querySelectorAll(bubbleSelectors);

                if (foundBubbles && foundBubbles.length > 0) {
                    foundBubbles.forEach(bubble => {
                        // Determine Role:
                        const isUser = bubble.matches('[data-role="user"]') ||
                                       bubble.matches('div[class*="f9bf7997"]') ||
                                       bubble.matches('div[class*="fa81"]') ||
                                       bubble.matches('div[class*="dad65929"]') ||
                                       (bubble.className && (
                                           bubble.className.includes('f9bf7997') ||
                                           bubble.className.includes('fa81') ||
                                           bubble.className.includes('dad65929') ||
                                           bubble.className.toLowerCase().includes('user')
                                       )) ||
                                       bubble.querySelector('[class*="f9bf7997"], [class*="fa81"], [class*="dad65929"], [data-role="user"], [class*="user"]') !== null;

                        const role = isUser ? 'user' : 'assistant';

                        // Extract DeepSeek-R1 Thinking / Reasoning Chain
                        let thinking = null;
                        if (!isUser) {
                            const thinkElem = bubble.querySelector(
                                '.ds-think, .ds-think-content, div[class*="f6d670"], [class*="think"], [class*="reasoning"], details'
                            );
                            if (thinkElem) {
                                const thought = extractFormattedNode(thinkElem);
                                if (thought && thought.length > 2) {
                                    thinking = thought;
                                }
                            }
                        }

                        // Extract Message Content
                        let content = '';
                        if (isUser) {
                            const userTextElem = bubble.querySelector(
                                '[class*="f9bf7997"], [class*="fa81"], [class*="dad65929"], [class*="text"], p'
                            ) || bubble;
                            content = extractFormattedNode(userTextElem);
                        } else {
                            const assistantContentElem = bubble.querySelector(
                                '.ds-markdown, [class*="markdown"], [class*="content"]'
                            ) || bubble;

                            // Clone to strip thinking block from main text
                            const clone = assistantContentElem.cloneNode(true);
                            clone.querySelectorAll(
                                '.ds-think, .ds-think-content, div[class*="f6d670"], [class*="think"], [class*="reasoning"], details'
                            ).forEach(el => el.remove());

                            content = extractFormattedNode(clone);
                        }

                        // Extract timestamp if present
                        let timestamp = null;
                        const timeElem = bubble.querySelector('time, [class*="time"], [data-timestamp]');
                        if (timeElem) {
                            const tsAttr = timeElem.getAttribute('datetime') || timeElem.getAttribute('data-timestamp');
                            if (tsAttr) {
                                const parsed = Date.parse(tsAttr);
                                if (!isNaN(parsed)) timestamp = parsed;
                            }
                        }

                        // Validate and deduplicate
                        if (content && content.length > 0 && !seenContents.has(content)) {
                            seenContents.add(content);
                            extractedMessages.push({
                                role: role,
                                content: content,
                                thinking: thinking,
                                model: isUser ? null : 'DeepSeek',
                                timestamp: timestamp
                            });
                        }
                    });
                }

                // 4. Fallback if obfuscated class hashes changed
                if (extractedMessages.length === 0) {
                    const allMarkdowns = document.querySelectorAll('.ds-markdown, [class*="markdown-body"], article, main div');
                    allMarkdowns.forEach((el, index) => {
                        const txt = extractFormattedNode(el);
                        if (txt && txt.length > 20 && !seenContents.has(txt)) {
                            seenContents.add(txt);
                            extractedMessages.push({
                                role: index % 2 === 0 ? 'user' : 'assistant',
                                content: txt,
                                thinking: null,
                                model: index % 2 === 0 ? null : 'DeepSeek'
                            });
                        }
                    });
                }

                return JSON.stringify({
                    success: true,
                    title: pageTitle || 'DeepSeek Chat',
                    messages: extractedMessages
                });
            } catch (err) {
                return JSON.stringify({
                    success: false,
                    error: err.toString(),
                    messages: []
                });
            }
        })();
    """.trimIndent()
}
