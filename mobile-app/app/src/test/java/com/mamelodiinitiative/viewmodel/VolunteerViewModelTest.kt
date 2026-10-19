package com.mamelodiinitiative.viewmodel

import com.mamelodiinitiative.data.VolunteerRepository
import com.mamelodiinitiative.data.models.VolunteerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class VolunteerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: VolunteerRepository
    private lateinit var viewModel: VolunteerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = VolunteerViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateApplication updates uiState`() {
        val app = VolunteerApplication(fullName = "John Doe")
        viewModel.updateApplication(app)
        assertEquals(app, viewModel.uiState.value.application)
    }

    @Test
    fun `resetApplication clears uiState`() {
        viewModel.updateApplication(VolunteerApplication(fullName = "John Doe"))
        viewModel.resetApplication()
        assertEquals("", viewModel.uiState.value.application.fullName)
    }

    @Test
    fun `submitApplication success updates isSuccess`() = runTest {
        whenever(repository.submitApplication(any())).thenReturn(Result.success(Unit))
        
        viewModel.submitApplication()
        
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `submitApplication failure updates error`() = runTest {
        val errorMessage = "Network Error"
        whenever(repository.submitApplication(any())).thenReturn(Result.failure(Exception(errorMessage)))
        
        viewModel.submitApplication()
        advanceUntilIdle()
        
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isSuccess)
    }
}
