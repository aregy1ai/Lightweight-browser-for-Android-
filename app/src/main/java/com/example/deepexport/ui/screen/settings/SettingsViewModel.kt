package com.example.deepexport.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deepexport.domain.model.AppSettings
import com.example.deepexport.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settingsFlow,
        _uiState
    ) { settings, currentUiState ->
        currentUiState.copy(
            settings = settings,
            thresholdInput = if (currentUiState.errorMessage == null) settings.threshold.toString() else currentUiState.thresholdInput
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onThresholdChange(newThreshold: Double) {
        viewModelScope.launch {
            if (newThreshold in 0.0..1.0) {
                _uiState.update { it.copy(errorMessage = null, thresholdInput = newThreshold.toString()) }
                settingsRepository.updateThreshold(newThreshold)
            } else {
                _uiState.update { it.copy(errorMessage = "يجب أن تكون القيمة بين 0.0 و 1.0") }
            }
        }
    }

    fun onThresholdInputChange(input: String) {
        _uiState.update { it.copy(thresholdInput = input) }
        val parsed = input.toDoubleOrNull()
        if (parsed != null && parsed in 0.0..1.0) {
            _uiState.update { it.copy(errorMessage = null) }
            viewModelScope.launch {
                settingsRepository.updateThreshold(parsed)
            }
        } else if (input.isNotBlank()) {
            _uiState.update { it.copy(errorMessage = "يجب أن تكون القيمة بين 0.0 و 1.0") }
        }
    }

    fun onFailOnNewMetricChange(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFailOnNewMetric(value)
        }
    }

    fun onFailOnMissingMetricChange(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateFailOnMissingMetric(value)
        }
    }

    fun onAutoScrollChange(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoScroll(value)
        }
    }

    fun saveSettings() {
        val current = uiState.value
        val parsedThreshold = current.thresholdInput.toDoubleOrNull() ?: current.settings.threshold
        viewModelScope.launch {
            val updated = current.settings.copy(
                threshold = parsedThreshold
            )
            settingsRepository.saveSettings(updated)
            _uiState.update { it.copy(isSaved = true, errorMessage = null) }
        }
    }

    fun resetSavedStatus() {
        _uiState.update { it.copy(isSaved = false) }
    }

    companion object {
        fun provideFactory(settingsRepository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(settingsRepository) as T
                }
            }
    }
}
