package com.example.deepexport.ui.screen.settings

import com.example.deepexport.domain.model.AppSettings

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val thresholdInput: String = "0.05",
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
