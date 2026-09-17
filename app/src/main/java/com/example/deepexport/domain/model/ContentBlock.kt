package com.example.deepexport.domain.model

sealed interface ContentBlock {
    data class Text(val text: String) : ContentBlock
    data class Code(val code: String, val language: String? = null) : ContentBlock
    data class Quote(val text: String) : ContentBlock
    data class Image(val url: String, val alt: String? = null) : ContentBlock
    data class Link(val text: String, val url: String) : ContentBlock
    data class File(val name: String, val size: String? = null) : ContentBlock
    data class Thinking(val thought: String) : ContentBlock
}
