package com.example.deepexport.domain.model

data class AppSettings(
    val threshold: Double = 0.05,
    val failOnNewMetric: Boolean = false,
    val failOnMissingMetric: Boolean = false,
    val autoScrollEnabled: Boolean = true,
    val autoSaveEnabled: Boolean = true,
    val defaultFormat: ExportFormat = ExportFormat.MARKDOWN
)
