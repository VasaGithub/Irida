package com.travelplanner.irida.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelplanner.irida.data.local.dao.AccessLogDao
import com.travelplanner.irida.data.local.entity.AccessLogEntity
import com.travelplanner.irida.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import com.travelplanner.irida.domain.UserRepository
import com.travelplanner.irida.data.local.entity.UserEntity


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val accessLogDao: AccessLogDao
) : ViewModel() {
    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

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
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Error de login") }
        }
    }

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
                    authRepository.sendVerificationEmail()
                    _currentUsername.value = username
                    Log.i("AuthVM", "REGISTER uid=${user.uid}")
                    _state.value = AuthUiState.Success
                }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Error de registro") }
        }
    }


    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            authRepository.sendPasswordReset(email)
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Error al enviar email") }
        }
    }

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
