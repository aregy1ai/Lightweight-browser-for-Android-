package com.example.deepexport.domain.model

data class ExtractionResult(
    val conversation: ChatConversation,
    val diagnostics: ExtractionDiagnostics
)
