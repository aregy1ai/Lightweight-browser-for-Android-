package com.example.deepexport.domain.repository

import com.example.deepexport.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<AppSettings>
    suspend fun updateThreshold(threshold: Double)
    suspend fun updateFailOnNewMetric(value: Boolean)
    suspend fun updateFailOnMissingMetric(value: Boolean)
    suspend fun updateAutoScroll(value: Boolean)
    suspend fun updateAutoSave(value: Boolean)
    suspend fun saveSettings(settings: AppSettings)
}
