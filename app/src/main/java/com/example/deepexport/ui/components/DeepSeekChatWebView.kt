package com.example.deepexport.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView

/**
 * JavaScript Interface bridge attached to WebView for bi-directional DOM inspection
 * and extracted conversation communication.
 */
class DeepSeekWebInterface(
    private val onMessagePayloadReceived: (String) -> Unit,
    private val onDomInspected: (String) -> Unit = {},
    private val onErrorReceived: (String) -> Unit = {}
) {
    /**
     * Called by injected JavaScript when full extracted JSON conversation data is ready.
     */
    @JavascriptInterface
    fun postMessage(jsonPayload: String) {
        onMessagePayloadReceived(jsonPayload)
    }

    /**
     * Called by injected JavaScript for DOM inspection logs and diagnostic info.
     */
    @JavascriptInterface
    fun onInspectionLog(logJson: String) {
        onDomInspected(logJson)
    }

    /**
     * Called by injected JavaScript if an error occurs during DOM traversal.
     */
    @JavascriptInterface
    fun onError(errorMessage: String) {
        onErrorReceived(errorMessage)
    }
}

/**
 * A dedicated Composable WebView component that loads the DeepSeek chat URL (or custom session URL),
 * configures secure JavaScript settings, injects a JavaScript interface for DOM inspection,
 * and exposes methods/callbacks for message extraction.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DeepSeekChatWebView(
    url: String,
    modifier: Modifier = Modifier,
    interfaceName: String = "DeepSeekExtractorBridge",
    onMessageExtracted: (String) -> Unit = {},
    onDomInspected: (String) -> Unit = {},
    onError: (String) -> Unit = {},
    onPageTitleReceived: (String) -> Unit = {},
    onPageFinished: (WebView, String) -> Unit = { _, _ -> },
    onWebViewCreated: (WebView) -> Unit = {}
) {
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val jsInterface = remember(onMessageExtracted, onDomInspected, onError) {
        DeepSeekWebInterface(
            onMessagePayloadReceived = onMessageExtracted,
            onDomInspected = onDomInspected,
            onErrorReceived = onError
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.let { wv ->
                (wv.parent as? ViewGroup)?.removeView(wv)
                wv.removeJavascriptInterface(interfaceName)
                wv.stopLoading()
                wv.clearHistory()
                wv.loadUrl("about:blank")
                wv.onPause()
                wv.removeAllViews()
                wv.destroy()
            }
            webViewInstance = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("deepseek_chat_webview"),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        userAgentString = settings.userAgentString + " DeepSeekExporterApp/1.0"
                    }

                    // Register JavaScript interface for DOM inspection & message streaming
                    addJavascriptInterface(jsInterface, interfaceName)

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, targetUrl: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, targetUrl, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, targetUrl: String?) {
                            super.onPageFinished(view, targetUrl)
                            isLoading = false
                            if (view != null && targetUrl != null) {
                                // Inject automatic DOM ready listener or helper
                                injectDomObserverHelper(view, interfaceName)
                                onPageFinished(view, targetUrl)
                            }
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            loadingProgress = newProgress / 100f
                            isLoading = newProgress < 100
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            super.onReceivedTitle(view, title)
                            title?.let { onPageTitleReceived(it) }
                        }
                    }

                    loadUrl(url)
                    webViewInstance = this
                    onWebViewCreated(this)
                }
            },
            update = { wv ->
                // Update URL if changed externally
                if (wv.url != url && url.isNotBlank() && wv.url != null) {
                    wv.loadUrl(url)
                }
            }
        )

        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadingProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .testTag("webview_loading_progress")
            )
        }
    }
}

/**
 * Injects a lightweight DOM watcher and helper functions connected to our JavaScript interface.
 */
private fun injectDomObserverHelper(webView: WebView, interfaceName: String) {
    val helperScript = """
        (function() {
            try {
                if (window.__deepseekBridgeInitialized) return;
                window.__deepseekBridgeInitialized = true;

                // Send initial inspection signal to Android
                if (window['$interfaceName'] && typeof window['$interfaceName'].onInspectionLog === 'function') {
                    window['$interfaceName'].onInspectionLog(JSON.stringify({
                        event: 'dom_ready',
                        url: location.href,
                        title: document.title,
                        timestamp: Date.now()
                    }));
                }
            } catch(e) {
                console.error("Failed to inject DOM helper:", e);
            }
        })();
    """.trimIndent()

    webView.evaluateJavascript(helperScript, null)
}
