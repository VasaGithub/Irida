package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelplanner.irida.data.PreferencesManager
import com.travelplanner.irida.data.local.entity.UserEntity
import com.travelplanner.irida.domain.AuthRepository
import com.travelplanner.irida.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    sealed class ProfileUiState {
        object Loading : ProfileUiState()
        data class Success(val user: UserEntity) : ProfileUiState()
        object SaveSuccess : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: run {
                _uiState.value = ProfileUiState.Error("No hay sesión activa")
                return@launch
            }
            val user = userRepository.getUserById(uid)
            _uiState.value = if (user != null) {
                ProfileUiState.Success(user)
            } else {
                ProfileUiState.Error("No se encontraron datos de usuario")
            }
        }
    }

    fun updateProfile(
        username: String,
        birthdate: String,
        phone: String,
        address: String,
        country: String,
        acceptEmails: Boolean
    ) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: run {
                _uiState.value = ProfileUiState.Error("No hay sesión activa")
                return@launch
            }
            val current = userRepository.getUserById(uid) ?: run {
                _uiState.value = ProfileUiState.Error("Usuario no encontrado")
                return@launch
            }

            // Validaciones
            if (username.isBlank()) {
                _uiState.value = ProfileUiState.Error("El nombre de usuario no puede estar vacío")
                return@launch
            }
            val usernameAvailable = userRepository.isUsernameAvailable(username.trim(), uid)
            if (!usernameAvailable) {
                _uiState.value = ProfileUiState.Error("Ese nombre de usuario ya está en uso")
                return@launch
            }

            userRepository.saveUser(
                current.copy(
                    username = username.trim(),
                    birthdate = birthdate.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    country = country.trim(),
                    acceptEmails = acceptEmails
                )
            )
            preferencesManager.saveUsername(username.trim())
            preferencesManager.saveBirthdate(birthdate.trim())
            _uiState.value = ProfileUiState.SaveSuccess
        }
    }

    /** Vuelve al estado Success recargando los datos tras guardar. */
    fun reloadAfterSave() {
        loadUser()
    }
}
