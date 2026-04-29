package com.travelplanner.irida.ui.viewmodels

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
import javax.inject.Inject

@HiltViewModel
class AccessLogViewModel @Inject constructor(
    private val accessLogDao: AccessLogDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class LogUiState {
        object Loading : LogUiState()
        data class Success(val logs: List<AccessLogEntity>) : LogUiState()
        data class Error(val message: String) : LogUiState()
    }

    private val _uiState = MutableStateFlow<LogUiState>(LogUiState.Loading)
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: run {
                _uiState.value = LogUiState.Error("No hay sesión activa")
                return@launch
            }
            val logs = accessLogDao.getLogsForUser(uid)
            _uiState.value = LogUiState.Success(logs)
        }
    }
}
