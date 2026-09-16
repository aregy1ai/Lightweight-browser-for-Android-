package com.example.deepexport.ui.screen.browser

import com.example.deepexport.domain.model.ChatConversation

data class BrowserUiState(
    val url: String = "https://chat.deepseek.com",
    val isExtracting: Boolean = false,
    val isLoading: Boolean = false,
    val webPageTitle: String = "",
    val conversation: ChatConversation? = null,
    val error: String? = null
)
