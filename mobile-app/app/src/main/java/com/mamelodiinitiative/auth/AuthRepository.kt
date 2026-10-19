package com.mamelodiinitiative.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun register(email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        requireNotNull(result.user) { "No user returned after registration." }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        requireNotNull(result.user) { "No user returned after sign-in." }
    }

    fun signOut() = auth.signOut()
}
