package com.example.deepexport.ui.screen.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepexport.DeepSeekExportApp
import com.example.deepexport.core.AppContainer
import com.example.deepexport.core.AppResult
import com.example.deepexport.core.sanitizeFileName
import com.example.deepexport.domain.model.ChatConversation
import com.example.deepexport.domain.model.ExportFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExportViewModel(
    private val container: AppContainer = DeepSeekExportApp.instance.container
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private var activeConversation: ChatConversation? = null

    init {
        viewModelScope.launch {
            container.activeConversation.collect { conv ->
                activeConversation = conv
                conv?.let {
                    val safeTitle = it.title.sanitizeFileName()
                    _uiState.value = _uiState.value.copy(
                        fileName = safeTitle
                    )
                }
            }
        }
    }

    fun setFileName(value: String) {
        _uiState.value = _uiState.value.copy(fileName = value)
    }

    fun setFormat(value: ExportFormat) {
        _uiState.value = _uiState.value.copy(format = value)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun exportNow(onSuccess: () -> Unit = {}) {
        val conv = activeConversation ?: run {
            _uiState.value = _uiState.value.copy(error = "لا توجد محادثة محددة للتصدير.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, error = null)
            val result = container.exportConversationUseCase(
                conversation = conv,
                format = _uiState.value.format,
                fileName = _uiState.value.fileName.ifBlank { "deepseek_chat" }
            )

            when (result) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportedResult = result.data,
                        result = "تم التصدير بنجاح: ${result.data.fileName}",
                        error = null
                    )
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        error = result.message
                    )
                }
                is AppResult.Loading -> Unit
            }
        }
    }
}
