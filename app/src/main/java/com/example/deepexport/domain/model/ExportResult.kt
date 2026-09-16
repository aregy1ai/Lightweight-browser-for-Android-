package com.example.deepexport.domain.model

data class ExportResult(
    val success: Boolean,
    val message: String,
    val filePath: String? = null,
    val fileName: String? = null,
    val format: ExportFormat? = null,
    val exportedAt: Long = System.currentTimeMillis()
)
