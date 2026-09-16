package com.example.deepexport.ui.screen.home

data class HomeUiState(
    val url: String = "https://chat.deepseek.com",
    val recentExportCount: Int = 0,
    val savedConversationCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
