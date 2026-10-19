package com.mamelodiinitiative.viewmodel

import com.mamelodiinitiative.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        whenever(repository.notificationsEnabled).thenReturn(flowOf(true))
        whenever(repository.highContrastEnabled).thenReturn(flowOf(false))
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects repository values`() = runTest {
        assertEquals(true, viewModel.notificationsEnabled.value)
        assertEquals(false, viewModel.highContrastEnabled.value)
    }

    @Test
    fun `toggleNotifications calls repository`() = runTest {
        viewModel.toggleNotifications(false)
        advanceUntilIdle()
        verify(repository).setNotificationsEnabled(false)
    }

    @Test
    fun `toggleHighContrast calls repository`() = runTest {
        viewModel.toggleHighContrast(true)
        advanceUntilIdle()
        verify(repository).setHighContrastEnabled(true)
    }
}
