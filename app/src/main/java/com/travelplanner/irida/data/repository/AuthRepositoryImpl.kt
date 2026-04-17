package com.travelplanner.irida.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.travelplanner.irida.domain.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    override suspend fun login(email: String, password: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                auth.signInWithEmailAndPassword(email, password).await().user!!
            }
        }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                auth.createUserWithEmailAndPassword(email, password).await().user!!
            }
        }

    override suspend fun sendVerificationEmail(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                auth.currentUser!!.sendEmailVerification().await()
                Unit
            }
        }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                auth.sendPasswordResetEmail(email).await()
                Unit
            }
        }

    override fun logout() = auth.signOut()
}
