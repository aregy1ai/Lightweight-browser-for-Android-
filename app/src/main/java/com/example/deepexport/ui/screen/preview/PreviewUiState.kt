package com.example.deepexport.ui.screen.preview

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat

data class PreviewUiState(
    val conversation: ChatConversation? = null,
    val selectedFormat: ExportFormat = ExportFormat.MARKDOWN,
    val formattedPreviewText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
