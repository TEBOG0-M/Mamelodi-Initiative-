package com.mamelodiinitiative.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
    val message: String? = null
)

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun register(email: String, password: String) = authenticate(email, password, true)
    fun signIn(email: String, password: String) = authenticate(email, password, false)
    fun clearMessage() { _uiState.value = _uiState.value.copy(message = null) }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState(user = null)
    }

    private fun authenticate(email: String, password: String, register: Boolean) {
        val cleanEmail = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.value = _uiState.value.copy(message = "Enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(message = "Password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val result = if (register) repository.register(cleanEmail, password)
                         else repository.signIn(cleanEmail, password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState(user = it) },
                onFailure = { AuthUiState(user = repository.currentUser, message = it.friendlyMessage()) }
            )
        }
    }
}

private fun Throwable.friendlyMessage(): String {
    val s = localizedMessage.orEmpty().lowercase()
    return when {
        "already in use" in s -> "An account already exists for this email. Try signing in."
        "invalid credential" in s || "wrong password" in s || "password is invalid" in s ->
            "Email or password is incorrect."
        "no user record" in s || "user not found" in s ->
            "No account found for this email. Register first."
        "network" in s -> "Network error. Check your connection and try again."
        "too many requests" in s -> "Too many attempts. Wait a moment before trying again."
        else -> "Authentication failed. Check your details and try again."
    }
}
