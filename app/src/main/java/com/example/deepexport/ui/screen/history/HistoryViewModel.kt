package com.example.deepexport.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            container.loadHistoryUseCase().collect { historyList ->
                _uiState.value = _uiState.value.copy(
                    exportHistory = historyList,
                    isLoading = false
                )
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            container.exportRepository.clearHistory()
        }
    }
}
