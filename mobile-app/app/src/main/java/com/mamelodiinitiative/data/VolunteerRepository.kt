package com.mamelodiinitiative.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mamelodiinitiative.data.models.VolunteerApplication
import kotlinx.coroutines.tasks.await

class VolunteerRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun submitApplication(application: VolunteerApplication): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User must be authenticated to submit")
        
        val docRef = firestore.collection("volunteer_applications").document()
        val finalApp = application.copy(
            id = docRef.id,
            userId = user.uid,
            submittedAt = Timestamp.now()
        )
        
        docRef.set(finalApp).await()
    }
}
