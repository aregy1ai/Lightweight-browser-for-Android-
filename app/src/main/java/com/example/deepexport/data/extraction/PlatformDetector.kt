package com.example.deepexport.data.extraction

import com.example.deepexport.domain.model.Platform

object PlatformDetector {
    fun detectFromUrl(url: String?): Platform {
        return Platform.fromUrl(url)
    }

    fun detect(url: String?, pageTitle: String? = null): Platform {
        val fromUrl = detectFromUrl(url)
        if (fromUrl != Platform.GENERIC) return fromUrl

        val titleLower = pageTitle?.lowercase() ?: ""
        return when {
            titleLower.contains("deepseek") -> Platform.DEEPSEEK
            titleLower.contains("chatgpt") || titleLower.contains("openai") -> Platform.CHATGPT
            titleLower.contains("claude") || titleLower.contains("anthropic") -> Platform.CLAUDE
            titleLower.contains("gemini") -> Platform.GEMINI
            titleLower.contains("perplexity") -> Platform.PERPLEXITY
            else -> Platform.GENERIC
        }
    }
}
