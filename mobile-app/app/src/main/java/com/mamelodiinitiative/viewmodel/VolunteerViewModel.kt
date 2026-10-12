package com.mamelodiinitiative.viewmodel

import androidx.lifecycle.ViewModel
import com.mamelodiinitiative.data.models.VolunteerApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VolunteerViewModel : ViewModel() {
    private val _application = MutableStateFlow(VolunteerApplication())
    val application: StateFlow<VolunteerApplication> = _application.asStateFlow()

    fun updateApplication(application: VolunteerApplication) { _application.value = application }
    fun resetApplication() { _application.value = VolunteerApplication() }
    fun isValid(): Boolean = with(_application.value) {
        fullName.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
    }
}
