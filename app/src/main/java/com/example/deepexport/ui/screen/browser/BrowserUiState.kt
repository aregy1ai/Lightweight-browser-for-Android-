package com.example.deepexport.ui.screen.browser

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExtractionDiagnostics
import com.example.deepexport.domain.model.Platform

data class BrowserUiState(
    val url: String = "https://chat.deepseek.com",
    val detectedPlatform: Platform = Platform.DEEPSEEK,
    val isExtracting: Boolean = false,
    val isLoading: Boolean = false,
    val statusMessage: String = "",
    val enableLongConversationScroll: Boolean = true,
    val webPageTitle: String = "",
    val conversation: ChatConversation? = null,
    val diagnostics: ExtractionDiagnostics? = null,
    val error: String? = null
)
