package com.example.deepexport.ui.screen.history

import com.example.deepexport.domain.model.ExportResult

data class HistoryUiState(
    val exportHistory: List<ExportResult> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
