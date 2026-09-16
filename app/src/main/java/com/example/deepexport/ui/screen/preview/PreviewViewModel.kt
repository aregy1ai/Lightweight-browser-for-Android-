package com.example.deepexport.ui.screen.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreviewViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            container.activeConversation.collect { conv ->
                _uiState.value = _uiState.value.copy(conversation = conv)
                updatePreviewText(conv, _uiState.value.selectedFormat)
            }
        }
    }

    fun setFormat(format: ExportFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
        updatePreviewText(_uiState.value.conversation, format)
    }

    private fun updatePreviewText(conversation: ChatConversation?, format: ExportFormat) {
        if (conversation == null) {
            _uiState.value = _uiState.value.copy(formattedPreviewText = "")
            return
        }
        val text = when (format) {
            ExportFormat.TXT -> container.txtExporter.export(conversation)
            ExportFormat.MARKDOWN -> container.markdownExporter.export(conversation)
            ExportFormat.JSON -> container.jsonExporter.export(conversation)
        }
        _uiState.value = _uiState.value.copy(formattedPreviewText = text)
    }
}
