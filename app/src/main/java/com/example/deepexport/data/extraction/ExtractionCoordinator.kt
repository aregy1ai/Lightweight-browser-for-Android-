package com.example.deepexport.data.extraction

import android.webkit.WebView
import com.example.deepexport.core.AppResult
import com.example.deepexport.data.extraction.adapter.ChatGPTAdapter
import com.example.deepexport.data.extraction.adapter.ClaudeAdapter
import com.example.deepexport.data.extraction.adapter.DeepSeekAdapter
import com.example.deepexport.data.extraction.adapter.GeminiAdapter
import com.example.deepexport.data.extraction.adapter.GenericWebAdapter
import com.example.deepexport.data.extraction.adapter.PerplexityAdapter
import com.example.deepexport.data.extraction.adapter.PlatformAdapter
import com.example.deepexport.data.extraction.pipeline.ConversationValidator
import com.example.deepexport.data.extraction.pipeline.TextProcessingPipeline
import com.example.deepexport.data.extraction.scroll.ConversationScrollLoader
import com.example.deepexport.domain.model.ExtractionDiagnostics
import com.example.deepexport.domain.model.ExtractionResult
import com.example.deepexport.domain.model.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class ExtractionCoordinator(
    private val scrollLoader: ConversationScrollLoader = ConversationScrollLoader()
) {

    private val adapters: Map<Platform, PlatformAdapter> = mapOf(
        Platform.DEEPSEEK to DeepSeekAdapter(),
        Platform.CHATGPT to ChatGPTAdapter(),
        Platform.CLAUDE to ClaudeAdapter(),
        Platform.GEMINI to GeminiAdapter(),
        Platform.PERPLEXITY to PerplexityAdapter(),
        Platform.GENERIC to GenericWebAdapter()
    )

    fun getAdapter(platform: Platform): PlatformAdapter {
        return adapters[platform] ?: adapters[Platform.GENERIC]!!
    }

    suspend fun extract(
        webView: WebView,
        enableScrollLoader: Boolean = false,
        onStatusUpdate: (String) -> Unit = {}
    ): AppResult<ExtractionResult> = withContext(Dispatchers.Main) {
        val startTime = System.currentTimeMillis()
        try {
            val currentUrl = webView.url ?: ""
            val pageTitle = webView.title ?: ""
            val platform = PlatformDetector.detect(currentUrl, pageTitle)
            val adapter = getAdapter(platform)

            onStatusUpdate("تم اكتشاف المنصة: ${platform.displayName}")

            if (enableScrollLoader) {
                onStatusUpdate("جارٍ التمرير لتحميل محتوى المحادثة الطويلة...")
                scrollLoader.prepareLongConversation(webView, onStatusUpdate)
            }

            onStatusUpdate("جارٍ استخراج الرسائل...")
            var usedStrategy = "Primary DOM Strategy"

            // 1. Try Primary script
            val primaryScript = adapter.getPrimaryScript()
            val primaryRaw = evalJs(webView, primaryScript)
            var conversation = adapter.parseJson(primaryRaw, currentUrl)

            // 2. If primary returned no messages, try Fallback script
            if (conversation == null || conversation.messages.isEmpty()) {
                onStatusUpdate("استخدام استراتيجية الاستخراج الاحتياطية...")
                usedStrategy = "Fallback DOM Strategy"
                val fallbackScript = adapter.getFallbackScript()
                val fallbackRaw = evalJs(webView, fallbackScript)
                conversation = adapter.parseJson(fallbackRaw, currentUrl)
            }

            // 3. If still empty and not Generic, try GenericWebAdapter
            if ((conversation == null || conversation.messages.isEmpty()) && platform != Platform.GENERIC) {
                onStatusUpdate("استخدام الاستراتيجية العامة...")
                usedStrategy = "Universal Fallback Strategy"
                val genericAdapter = adapters[Platform.GENERIC]!!
                val genericRaw = evalJs(webView, genericAdapter.getPrimaryScript())
                conversation = genericAdapter.parseJson(genericRaw, currentUrl)?.copy(platform = platform)
            }

            if (conversation == null || conversation.messages.isEmpty()) {
                return@withContext AppResult.Error(
                    IllegalStateException("لم يتم العثور على أي رسائل في هذه الصفحة. تأكد من فتح المحادثة بعد اكتمال تحميلها.")
                )
            }

            // Normalization Pipeline
            val cleanedConversation = TextProcessingPipeline.process(conversation)
            val validation = ConversationValidator.validate(cleanedConversation)

            if (!validation.isValid) {
                return@withContext AppResult.Error(
                    IllegalStateException(validation.errorMessage ?: "فشل التحقق من المحادثة المستخرجة.")
                )
            }

            val duration = System.currentTimeMillis() - startTime
            val diagnostics = ExtractionDiagnostics(
                platform = platform,
                strategyName = usedStrategy,
                messageCount = cleanedConversation.messages.size,
                durationMs = duration,
                hasLongConversationScrolled = enableScrollLoader,
                warnings = validation.warnings
            )

            AppResult.Success(
                ExtractionResult(
                    conversation = cleanedConversation,
                    diagnostics = diagnostics
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e, "فشل الاستخراج: ${e.localizedMessage}")
        }
    }

    private suspend fun evalJs(webView: WebView, script: String): String =
        suspendCancellableCoroutine { continuation ->
            webView.evaluateJavascript(script) { result ->
                if (continuation.isActive) {
                    continuation.resume(result ?: "")
                }
            }
        }
}
