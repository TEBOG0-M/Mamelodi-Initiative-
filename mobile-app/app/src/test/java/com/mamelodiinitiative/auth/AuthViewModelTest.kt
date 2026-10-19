package com.mamelodiinitiative.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        // Default mock behavior for current user to be null
        whenever(repository.currentUser).thenReturn(null)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn with invalid email updates message`() = runTest {
        viewModel.signIn("invalid-email", "password123")
        assertEquals("Enter a valid email address.", viewModel.uiState.value.message)
    }

    @Test
    fun `signIn with short password updates message`() = runTest {
        viewModel.signIn("test@example.com", "123")
        assertEquals("Password must be at least 6 characters.", viewModel.uiState.value.message)
    }

    @Test
    fun `signIn with valid credentials calls repository and updates user`() = runTest {
        val mockUser: FirebaseUser = mock()
        whenever(repository.signIn(any(), any())).thenReturn(Result.success(mockUser))
        
        viewModel.signIn("test@example.com", "password123")
        
        // Before idle, it should be loading
        advanceUntilIdle()
        
        assertEquals(mockUser, viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.message)
    }

    @Test
    fun `signOut clears user state`() = runTest {
        viewModel.signOut()
        assertNull(viewModel.uiState.value.user)
    }

    @Test
    fun `clearMessage resets error message`() = runTest {
        viewModel.signIn("invalid", "123")
        viewModel.clearMessage()
        assertNull(viewModel.uiState.value.message)
    }
}
