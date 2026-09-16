package com.example.deepexport.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
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
                    savedConversationCount = saved.size
                )
            }
        }
    }

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(url = url, error = null)
    }

    fun resetToDefaultUrl() {
        _uiState.value = _uiState.value.copy(url = "https://chat.deepseek.com")
    }
}
