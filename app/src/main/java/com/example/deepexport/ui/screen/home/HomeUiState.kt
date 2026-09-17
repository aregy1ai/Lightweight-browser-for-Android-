package com.example.deepexport.ui.screen.home

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

data class HomeUiState(
    val url: String = Platform.DEEPSEEK.defaultUrl,
    val selectedPlatform: Platform = Platform.DEEPSEEK,
    val recentExportCount: Int = 0,
    val savedConversationCount: Int = 0,
    val recentConversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
