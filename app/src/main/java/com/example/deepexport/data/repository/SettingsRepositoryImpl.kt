package com.example.deepexport.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.deepexport.domain.model.AppSettings
import com.example.deepexport.domain.model.ExportFormat
import com.example.deepexport.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val THRESHOLD = doublePreferencesKey("benchmark_threshold")
        val FAIL_ON_NEW_METRIC = booleanPreferencesKey("fail_on_new_metric")
        val FAIL_ON_MISSING_METRIC = booleanPreferencesKey("fail_on_missing_metric")
        val AUTO_SCROLL = booleanPreferencesKey("auto_scroll_enabled")
        val DEFAULT_FORMAT = stringPreferencesKey("default_export_format")
    }

    override val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            threshold = prefs[PreferencesKeys.THRESHOLD] ?: 0.05,
            failOnNewMetric = prefs[PreferencesKeys.FAIL_ON_NEW_METRIC] ?: false,
            failOnMissingMetric = prefs[PreferencesKeys.FAIL_ON_MISSING_METRIC] ?: false,
            autoScrollEnabled = prefs[PreferencesKeys.AUTO_SCROLL] ?: true,
            defaultFormat = prefs[PreferencesKeys.DEFAULT_FORMAT]?.let {
                try { ExportFormat.valueOf(it) } catch (_: Exception) { ExportFormat.MARKDOWN }
            } ?: ExportFormat.MARKDOWN
        )
    }

    override suspend fun updateThreshold(threshold: Double) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THRESHOLD] = threshold
        }
    }

    override suspend fun updateFailOnNewMetric(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.FAIL_ON_NEW_METRIC] = value
        }
    }

    override suspend fun updateFailOnMissingMetric(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.FAIL_ON_MISSING_METRIC] = value
        }
    }

    override suspend fun updateAutoScroll(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AUTO_SCROLL] = value
        }
    }

    override suspend fun saveSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THRESHOLD] = settings.threshold
            prefs[PreferencesKeys.FAIL_ON_NEW_METRIC] = settings.failOnNewMetric
            prefs[PreferencesKeys.FAIL_ON_MISSING_METRIC] = settings.failOnMissingMetric
            prefs[PreferencesKeys.AUTO_SCROLL] = settings.autoScrollEnabled
            prefs[PreferencesKeys.DEFAULT_FORMAT] = settings.defaultFormat.name
        }
    }
}
