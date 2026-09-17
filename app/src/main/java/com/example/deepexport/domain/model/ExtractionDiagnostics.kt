package com.example.deepexport.domain.model

data class ExtractionDiagnostics(
    val platform: Platform,
    val strategyName: String,
    val messageCount: Int,
    val durationMs: Long,
    val hasLongConversationScrolled: Boolean = false,
    val warnings: List<String> = emptyList()
)
