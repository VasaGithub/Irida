package com.travelplanner.irida.domain

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isEmailVerified: Boolean
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun sendVerificationEmail(): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    fun logout()
}
