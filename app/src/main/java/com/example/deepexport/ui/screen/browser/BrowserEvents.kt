package com.example.deepexport.ui.screen.browser

import com.example.deepexport.domain.model.ChatConversation

sealed interface BrowserEvents {
    data class NavigateToPreview(val conversation: ChatConversation) : BrowserEvents
    data class ShowToast(val message: String) : BrowserEvents
}
