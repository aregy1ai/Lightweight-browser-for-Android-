package com.example.deepexport.data.web

import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView

class WebViewBridge(
    private val onMessageExtracted: (String) -> Unit
) {
    @JavascriptInterface
    fun postExtractedData(jsonPayload: String) {
        onMessageExtracted(jsonPayload)
    }

    companion object {
        fun cleanupWebView(webView: WebView?) {
            webView?.let { wv ->
                (wv.parent as? ViewGroup)?.removeView(wv)
                wv.stopLoading()
                wv.clearHistory()
                wv.loadUrl("about:blank")
                wv.onPause()
                wv.removeAllViews()
                wv.destroy()
            }
        }
    }
}
