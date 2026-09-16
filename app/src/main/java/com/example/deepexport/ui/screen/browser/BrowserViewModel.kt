package com.example.deepexport.ui.screen.browser

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import com.example.deepexport.core.AppResult
import com.example.deepexport.domain.model.ChatConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class BrowserViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun setUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url, error = null)
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun setPageTitle(title: String) {
        _uiState.value = _uiState.value.copy(webPageTitle = title)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun extractFromWebView(webView: WebView, onSuccess: (ChatConversation) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExtracting = true, error = null)

            val currentUrl = webView.url ?: _uiState.value.url
            val script = container.deepSeekWebRepository.getExtractionScript()

            val rawResult = withContext(Dispatchers.Main) {
                suspendCancellableCoroutine<String> { continuation ->
                    webView.evaluateJavascript(script) { result ->
                        continuation.resume(result ?: "")
                    }
                }
            }

            val parseResult = container.deepSeekWebRepository.parseResult(rawResult, currentUrl)
            when (parseResult) {
                is AppResult.Success -> {
                    val conversation = parseResult.data
                    container.setActiveConversation(conversation)
                    // Auto-save to local history
                    launch(Dispatchers.IO) {
                        container.saveConversationUseCase(conversation)
                    }
                    _uiState.value = _uiState.value.copy(
                        conversation = conversation,
                        isExtracting = false,
                        error = null
                    )
                    onSuccess(conversation)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExtracting = false,
                        error = parseResult.message
                    )
                }
                is AppResult.Loading -> Unit
            }
        }
    }
}
