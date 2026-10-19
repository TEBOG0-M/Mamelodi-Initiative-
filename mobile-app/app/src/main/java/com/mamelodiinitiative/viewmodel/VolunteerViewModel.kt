package com.mamelodiinitiative.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamelodiinitiative.data.VolunteerRepository
import com.mamelodiinitiative.data.models.VolunteerApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VolunteerUiState(
    val application: VolunteerApplication = VolunteerApplication(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class VolunteerViewModel(
    private val repository: VolunteerRepository = VolunteerRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(VolunteerUiState())
    val uiState: StateFlow<VolunteerUiState> = _uiState.asStateFlow()

    fun updateApplication(application: VolunteerApplication) {
        _uiState.value = _uiState.value.copy(application = application, error = null)
    }

    fun resetApplication() {
        _uiState.value = VolunteerUiState()
    }

    fun submitApplication() {
        if (_uiState.value.isLoading) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.submitApplication(_uiState.value.application)
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, isSuccess = true) },
                onFailure = { _uiState.value.copy(isLoading = false, error = it.localizedMessage ?: "Submission failed") }
            )
        }
    }
}
