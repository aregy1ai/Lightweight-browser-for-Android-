package com.example.deepexport.data.extraction.scroll

import android.webkit.WebView
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ConversationScrollLoader {

    companion object {
        val scrollScript = """
            (function() {
                try {
                    // Find scrollable container
                    function getScrollContainer() {
                        const candidates = [
                            document.querySelector('main'),
                            document.querySelector('[role="main"]'),
                            document.querySelector('.conversation-container'),
                            document.querySelector('.chat-history'),
                            document.documentElement,
                            document.body
                        ];
                        for (const el of candidates) {
                            if (el && el.scrollHeight > el.clientHeight + 100) {
                                return el;
                            }
                        }
                        return window;
                    }

                    const scroller = getScrollContainer();
                    if (scroller === window) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                    } else {
                        scroller.scrollTo({ top: 0, behavior: 'smooth' });
                    }
                    return "scrolled_to_top";
                } catch(e) {
                    return "error: " + e;
                }
            })();
        """.trimIndent()

        val scrollDownScript = """
            (function() {
                try {
                    function getScrollContainer() {
                        const candidates = [
                            document.querySelector('main'),
                            document.querySelector('[role="main"]'),
                            document.querySelector('.conversation-container'),
                            document.querySelector('.chat-history'),
                            document.documentElement,
                            document.body
                        ];
                        for (const el of candidates) {
                            if (el && el.scrollHeight > el.clientHeight + 100) {
                                return el;
                            }
                        }
                        return window;
                    }

                    const scroller = getScrollContainer();
                    if (scroller === window) {
                        window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                    } else {
                        scroller.scrollTo({ top: scroller.scrollHeight, behavior: 'smooth' });
                    }
                    return "scrolled_to_bottom";
                } catch(e) {
                    return "error: " + e;
                }
            })();
        """.trimIndent()
    }

    suspend fun prepareLongConversation(webView: WebView, onStatus: (String) -> Unit) {
        onStatus("جارٍ تحميل رسائل المحادثة السابقة (التمرير الذكي)...")
        // Scroll to top first to trigger older messages loading if any
        eval(webView, scrollScript)
        delay(600)
        // Scroll back down
        eval(webView, scrollDownScript)
        delay(600)
    }

    private suspend fun eval(webView: WebView, script: String): String =
        suspendCancellableCoroutine { continuation ->
            webView.evaluateJavascript(script) { result ->
                if (continuation.isActive) {
                    continuation.resume(result ?: "")
                }
            }
        }
}
