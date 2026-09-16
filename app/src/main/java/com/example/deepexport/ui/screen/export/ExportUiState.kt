package com.example.deepexport.ui.screen.export

import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.model.ExportResult

data class ExportUiState(
    val fileName: String = "deepseek_chat",
    val format: ExportFormat = ExportFormat.MARKDOWN,
    val isExporting: Boolean = false,
    val result: String? = null,
    val exportedResult: ExportResult? = null,
    val error: String? = null
)
