package com.example.deepexport.ui.screen.browser

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import com.example.deepexport.core.AppResult
import com.example.deepexport.data.extraction.PlatformDetector
import com.example.deepexport.domain.model.ChatConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BrowserViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun setUrl(url: String) {
        val platform = PlatformDetector.detect(url, _uiState.value.webPageTitle)
        _uiState.value = _uiState.value.copy(
            url = url,
            detectedPlatform = platform,
            error = null
        )
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun setPageTitle(title: String) {
        val platform = PlatformDetector.detect(_uiState.value.url, title)
        _uiState.value = _uiState.value.copy(
            webPageTitle = title,
            detectedPlatform = platform
        )
    }

    fun toggleLongConversationScroll(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableLongConversationScroll = enabled)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun extractFromWebView(webView: WebView, onSuccess: (ChatConversation) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExtracting = true,
                statusMessage = "جارٍ تهيئة المحرك...",
                error = null
            )

            val coordinator = container.extractionCoordinator
            val result = coordinator.extract(
                webView = webView,
                enableScrollLoader = _uiState.value.enableLongConversationScroll,
                onStatusUpdate = { status ->
                    _uiState.value = _uiState.value.copy(statusMessage = status)
                }
            )

            when (result) {
                is AppResult.Success -> {
                    val extractionResult = result.data
                    val conversation = extractionResult.conversation
                    container.setActiveConversation(conversation)

                    // Auto-save to local history in Room DB if enabled in settings
                    launch(Dispatchers.IO) {
                        val currentSettings = container.settingsRepository.settingsFlow.first()
                        if (currentSettings.autoSaveEnabled) {
                            container.saveConversationUseCase(conversation)
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        conversation = conversation,
                        diagnostics = extractionResult.diagnostics,
                        isExtracting = false,
                        statusMessage = "",
                        error = null
                    )
                    onSuccess(conversation)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExtracting = false,
                        statusMessage = "",
                        error = result.message
                    )
                }
                is AppResult.Loading -> Unit
            }
        }
    }
}
