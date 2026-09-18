package com.example.deepexport.ui.screen.settings

import com.example.deepexport.MainDispatcherRule
import com.example.deepexport.domain.model.AppSettings
import com.example.deepexport.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FakeSettingsRepository : SettingsRepository {
    val state = MutableStateFlow(AppSettings())
    override val settingsFlow: Flow<AppSettings> = state

    override suspend fun updateThreshold(threshold: Double) {
        state.value = state.value.copy(threshold = threshold)
    }

    override suspend fun updateFailOnNewMetric(value: Boolean) {
        state.value = state.value.copy(failOnNewMetric = value)
    }

    override suspend fun updateFailOnMissingMetric(value: Boolean) {
        state.value = state.value.copy(failOnMissingMetric = value)
    }

    override suspend fun updateAutoScroll(value: Boolean) {
        state.value = state.value.copy(autoScrollEnabled = value)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        state.value = settings
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has default settings and threshold`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        val state = viewModel.uiState.value
        assertEquals(0.05, state.settings.threshold, 0.001)
        assertFalse(state.settings.failOnNewMetric)
        assertFalse(state.settings.failOnMissingMetric)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onThresholdChange updates valid threshold and clears error`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onThresholdChange(0.15)
        advanceUntilIdle()

        assertEquals(0.15, repository.state.value.threshold, 0.001)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onThresholdChange rejects out of range threshold with error message`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onThresholdChange(1.5)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessage != null)
    }

    @Test
    fun `onFailOnNewMetricChange updates repository flag`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onFailOnNewMetricChange(true)
        advanceUntilIdle()

        assertTrue(repository.state.value.failOnNewMetric)
    }

    @Test
    fun `saveSettings marks isSaved true and updates repository`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onThresholdChange(0.10)
        advanceUntilIdle()
        viewModel.saveSettings()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
        assertEquals(0.10, repository.state.value.threshold, 0.001)

        viewModel.resetSavedStatus()
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
