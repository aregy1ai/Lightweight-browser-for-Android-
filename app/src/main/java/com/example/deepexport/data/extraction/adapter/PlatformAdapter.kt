package com.example.deepexport.data.extraction.adapter

import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform

interface PlatformAdapter {
    val platform: Platform
    fun getPrimaryScript(): String
    fun getFallbackScript(): String
    fun parseJson(raw: String, currentUrl: String): ChatConversation?
}
