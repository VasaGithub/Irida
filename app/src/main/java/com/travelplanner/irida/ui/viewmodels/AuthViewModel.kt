package com.travelplanner.irida.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.FirebaseTooManyRequestsException
import com.travelplanner.irida.data.PreferencesManager
import com.travelplanner.irida.data.local.dao.AccessLogDao
import com.travelplanner.irida.data.local.entity.AccessLogEntity
import com.travelplanner.irida.data.local.entity.UserEntity
import com.travelplanner.irida.domain.AuthRepository
import com.travelplanner.irida.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val accessLogDao: AccessLogDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ── Estado general de autenticación ───────────────────────────────────
    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }

    // ── Estado dedicado para el flujo de verificación de email ───────────
    sealed class VerifyUiState {
        object Idle : VerifyUiState()
        object Loading : VerifyUiState()
        object Verified : VerifyUiState()
        object ResendSuccess : VerifyUiState()
        data class Error(val message: String) : VerifyUiState()
    }

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _verifyState = MutableStateFlow<VerifyUiState>(VerifyUiState.Idle)
    val verifyState: StateFlow<VerifyUiState> = _verifyState.asStateFlow()

    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    val isLoggedIn get() = authRepository.currentUser != null
    val isEmailVerified get() = authRepository.isEmailVerified

    init {
        authRepository.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                _currentUsername.value = userRepository.getUserById(uid)?.username ?: ""
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess { user ->
                    accessLogDao.insert(
                        AccessLogEntity(
                            userId = user.uid,
                            action = "LOGIN",
                            datetime = LocalDateTime.now().toString()
                        )
                    )
                    _currentUsername.value = userRepository.getUserById(user.uid)?.username ?: ""
                    Log.i("AuthVM", "LOGIN uid=${user.uid}")
                    _state.value = AuthUiState.Success
                }
                .onFailure { _state.value = AuthUiState.Error(it.toSpanishMessage()) }
        }
    }

    // ── Registro ──────────────────────────────────────────────────────────
    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.register(email, password)
                .onSuccess { user ->
                    userRepository.saveUser(
                        UserEntity(
                            uid = user.uid,
                            email = email,
                            username = username,
                            birthdate = "",
                            address = "",
                            country = "",
                            phone = "",
                            acceptEmails = false
                        )
                    )
                    preferencesManager.saveUsername(username)
                    authRepository.sendVerificationEmail()
                    _currentUsername.value = username
                    Log.i("AuthVM", "REGISTER uid=${user.uid}")
                    _state.value = AuthUiState.Success
                }
                .onFailure { _state.value = AuthUiState.Error(it.toSpanishMessage()) }
        }
    }

    // ── Verificación de email ─────────────────────────────────────────────
    /** Recarga el usuario de Firebase y comprueba si ya verificó el email. */
    fun checkEmailVerified() {
        viewModelScope.launch {
            _verifyState.value = VerifyUiState.Loading
            authRepository.reloadUser()
                .onSuccess {
                    if (authRepository.isEmailVerified) {
                        Log.i("AuthVM", "Email verificado correctamente")
                        _verifyState.value = VerifyUiState.Verified
                    } else {
                        _verifyState.value = VerifyUiState.Error("Todavía no hemos recibido la verificación. Revisa tu bandeja de entrada.")
                    }
                }
                .onFailure {
                    _verifyState.value = VerifyUiState.Error(it.message ?: "Error al comprobar la verificación")
                }
        }
    }

    /** Reenvía el email de verificación al usuario actual. */
    fun resendVerificationEmail() {
        viewModelScope.launch {
            _verifyState.value = VerifyUiState.Loading
            authRepository.sendVerificationEmail()
                .onSuccess {
                    Log.i("AuthVM", "Email de verificación reenviado")
                    _verifyState.value = VerifyUiState.ResendSuccess
                }
                .onFailure {
                    _verifyState.value = VerifyUiState.Error(it.message ?: "Error al reenviar el email")
                }
        }
    }

    fun resetVerifyState() {
        _verifyState.value = VerifyUiState.Idle
    }

    // ── Recuperación de contraseña ─────────────────────────────────────────
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.sendPasswordReset(email)
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.toSpanishMessage()) }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid
            uid?.let {
                accessLogDao.insert(
                    AccessLogEntity(
                        userId = it,
                        action = "LOGOUT",
                        datetime = LocalDateTime.now().toString()
                    )
                )
            }
            authRepository.logout()
            _currentUsername.value = ""
            Log.i("AuthVM", "LOGOUT uid=$uid")
            _state.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }
}

private fun Throwable.toSpanishMessage(): String = when (this) {
    is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
    is FirebaseAuthInvalidUserException        -> "No existe ninguna cuenta con este correo"
    is FirebaseAuthUserCollisionException      -> "Ya existe una cuenta con este correo"
    is FirebaseTooManyRequestsException        -> "Demasiados intentos fallidos. Inténtalo más tarde"
    else -> "Error de autenticación. Inténtalo de nuevo"
}
