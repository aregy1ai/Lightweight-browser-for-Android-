package com.example.deepexport.data.web

object ExtractionJs {

    /**
     * JavaScript code evaluated inside DeepSeek Webview.
     * Extracts conversation messages, roles, thinking content, and returns a JSON string.
     */
    val script: String = """
        (function() {
            try {
                // 1. Get Conversation Title
                let pageTitle = document.title || 'محادثة DeepSeek';
                const headerTitleElem = document.querySelector('header h1, header [class*="title"], [class*="chat-header"] [class*="title"]');
                if (headerTitleElem && headerTitleElem.innerText.trim()) {
                    pageTitle = headerTitleElem.innerText.trim();
                }

                const extractedMessages = [];

                // 2. Selectors for DeepSeek chat items
                // DeepSeek Web currently uses specific message container structures:
                // e.g., elements containing user prompt or assistant response
                const messageContainers = document.querySelectorAll(
                    'div[class*="chat-item"], div[class*="chat-message"], div[data-role], div[class*="fbb737a4"], div[class*="chat-content"], div[class*="f6d670"]'
                );

                // Helper to clean up text while preserving line breaks & code blocks
                function extractFormattedText(node) {
                    if (!node) return '';
                    const clone = node.cloneNode(true);

                    // Replace code blocks with fenced blocks
                    const codeBlocks = clone.querySelectorAll('pre');
                    codeBlocks.forEach(pre => {
                        const codeElem = pre.querySelector('code');
                        const langClass = codeElem ? (codeElem.className.match(/language-(\w+)/) || [])[1] : '';
                        const lang = langClass || '';
                        const codeText = pre.innerText || '';
                        const replacement = document.createTextNode('\n```' + lang + '\n' + codeText.trim() + '\n```\n');
                        pre.parentNode.replaceChild(replacement, pre);
                    });

                    return (clone.innerText || clone.textContent || '').trim();
                }

                // Strategy A: Check specific DeepSeek containers
                const chatListWrapper = document.querySelector('div[class*="chat-list"], div[class*="chat-message-list"], main, [role="main"]');
                const candidateNodes = chatListWrapper ? 
                    chatListWrapper.querySelectorAll(':scope > div, div[class*="item"], div[class*="message"], div[class*="bubble"]') :
                    messageContainers;

                const seenContents = new Set();

                // Strategy B: Find distinct user and bot message turns
                const allMarkdownDivs = document.querySelectorAll('.ds-markdown, [class*="ds-markdown"], [class*="markdown-body"]');
                const userPrompts = document.querySelectorAll('div[class*="user"], div[class*="prompt"], [data-role="user"]');

                // Let's inspect general message turns in DOM order
                const messageNodes = document.querySelectorAll(
                    '[class*="chat-message"], [class*="chat-item"], [data-role="user"], [data-role="assistant"], div[class*="fbb737a4"], div[class*="f6d670"]'
                );

                if (messageNodes && messageNodes.length > 0) {
                    messageNodes.forEach((el, idx) => {
                        const isUser = el.matches('[data-role="user"]') || 
                                       el.className.toLowerCase().includes('user') ||
                                       el.querySelector('[class*="user"]') !== null ||
                                       el.className.toLowerCase().includes('fbb737a4'); // User bubble in DeepSeek web

                        const role = isUser ? 'user' : 'assistant';

                        // Thinking element in DeepSeek R1
                        let thinkingText = '';
                        const thinkingElem = el.querySelector('[class*="thinking"], [class*="reasoning"], [class*="f6d670"], details');
                        if (thinkingElem) {
                            thinkingText = extractFormattedText(thinkingElem);
                        }

                        // Main content (excluding thinking if separate)
                        let mainContent = '';
                        const mdElem = el.querySelector('.ds-markdown, [class*="markdown"], [class*="content"]');
                        if (mdElem) {
                            mainContent = extractFormattedText(mdElem);
                        } else {
                            mainContent = extractFormattedText(el);
                        }

                        if (mainContent && !seenContents.has(mainContent) && mainContent.length > 1) {
                            seenContents.add(mainContent);
                            extractedMessages.push({
                                role: role,
                                content: mainContent,
                                thinking: thinkingText || null,
                                model: isUser ? null : 'DeepSeek'
                            });
                        }
                    });
                }

                // Fallback Strategy C: If no messages matched above, extract all markdown blocks & user inputs
                if (extractedMessages.length === 0) {
                    const fallbackElems = document.querySelectorAll('.ds-markdown, pre, [class*="markdown"], p');
                    if (fallbackElems.length > 0) {
                        let combined = [];
                        fallbackElems.forEach(el => {
                            const txt = el.innerText ? el.innerText.trim() : '';
                            if (txt && txt.length > 5) {
                                combined.push(txt);
                            }
                        });
                        if (combined.length > 0) {
                            extractedMessages.push({
                                role: 'assistant',
                                content: combined.join('\n\n'),
                                thinking: null,
                                model: 'DeepSeek'
                            });
                        }
                    }
                }

                return JSON.stringify({
                    success: true,
                    title: pageTitle,
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
