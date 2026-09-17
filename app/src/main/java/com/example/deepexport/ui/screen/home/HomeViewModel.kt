package com.example.deepexport.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import com.example.deepexport.data.extraction.PlatformDetector
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            container.exportRepository.getExportHistory().collect { history ->
                _uiState.value = _uiState.value.copy(
                    recentExportCount = history.size
                )
            }
        }
        viewModelScope.launch {
            container.conversationRepository.getSavedConversations().collect { saved ->
                _uiState.value = _uiState.value.copy(
                    savedConversationCount = saved.size,
                    recentConversations = saved.take(5)
                )
            }
        }
    }

    fun updateUrl(url: String) {
        val detected = PlatformDetector.detectFromUrl(url)
        _uiState.value = _uiState.value.copy(
            url = url,
            selectedPlatform = detected,
            error = null
        )
    }

    fun selectPlatform(platform: Platform) {
        _uiState.value = _uiState.value.copy(
            url = platform.defaultUrl,
            selectedPlatform = platform,
            error = null
        )
    }

    fun openSavedConversation(conversation: ChatConversation, onReady: () -> Unit) {
        container.setActiveConversation(conversation)
        onReady()
    }

    fun resetToDefaultUrl() {
        _uiState.value = _uiState.value.copy(
            url = Platform.DEEPSEEK.defaultUrl,
            selectedPlatform = Platform.DEEPSEEK
        )
    }
}
