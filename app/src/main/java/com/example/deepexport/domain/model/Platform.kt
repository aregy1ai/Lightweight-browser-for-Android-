package com.example.deepexport.domain.model

enum class Platform(
    val displayName: String,
    val domains: List<String>,
    val defaultUrl: String
) {
    DEEPSEEK(
        displayName = "DeepSeek",
        domains = listOf("chat.deepseek.com", "deepseek.com"),
        defaultUrl = "https://chat.deepseek.com"
    ),
    CHATGPT(
        displayName = "ChatGPT",
        domains = listOf("chatgpt.com", "chat.openai.com"),
        defaultUrl = "https://chatgpt.com"
    ),
    CLAUDE(
        displayName = "Claude",
        domains = listOf("claude.ai"),
        defaultUrl = "https://claude.ai"
    ),
    GEMINI(
        displayName = "Gemini",
        domains = listOf("gemini.google.com"),
        defaultUrl = "https://gemini.google.com"
    ),
    PERPLEXITY(
        displayName = "Perplexity",
        domains = listOf("perplexity.ai"),
        defaultUrl = "https://www.perplexity.ai"
    ),
    GENERIC(
        displayName = "موقع ويب عام",
        domains = emptyList(),
        defaultUrl = "https://www.google.com"
    );

    companion object {
        fun fromUrl(url: String?): Platform {
            if (url.isNullOrBlank()) return GENERIC
            val lower = url.lowercase()
            return when {
                lower.contains("deepseek") -> DEEPSEEK
                lower.contains("chatgpt") || lower.contains("openai.com") -> CHATGPT
                lower.contains("claude.ai") -> CLAUDE
                lower.contains("gemini.google.com") -> GEMINI
                lower.contains("perplexity.ai") -> PERPLEXITY
                else -> GENERIC
            }
        }
    }
}
