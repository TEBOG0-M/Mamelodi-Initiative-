package com.mamelodiinitiative.data.models

import com.google.firebase.Timestamp

data class VolunteerApplication(
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val skills: String = "",
    val programmePreference: String = "",
    val availability: String = "",
    val submittedAt: Timestamp? = null
)
