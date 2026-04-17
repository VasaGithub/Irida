package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

sealed class TripListUiState {
    object Loading : TripListUiState()
    data class Success(val trips: List<Trip>) : TripListUiState()
    data class Error(val message: String) : TripListUiState()
}

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val repository: TripRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripListUiState>(TripListUiState.Loading)
    val uiState: StateFlow<TripListUiState> = _uiState.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private val currentUserId get() = auth.currentUser?.uid ?: ""

    init {
        viewModelScope.launch {
            repository.getTripsStream(currentUserId).collect { trips ->
                _uiState.value = TripListUiState.Success(trips)
            }
        }
    }

    fun addTrip(
        title: String,
        description: String,
        destination: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        emoji: String = "✈️",
        budget: Double = 0.0
    ): Boolean {
        if (!validate(title, description, startDate, endDate)) return false
        val trip = Trip(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            destination = destination.trim(),
            startDate = startDate!!,
            endDate = endDate!!,
            emoji = emoji,
            budget = budget,
            budgetSpent = 0.0,
            nights = endDate.compareTo(startDate).coerceAtLeast(0)
        )
        viewModelScope.launch { repository.addTrip(trip) }
        return true
    }

    fun editTrip(
        id: String,
        title: String,
        description: String,
        destination: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        emoji: String,
        budget: Double,
        budgetSpent: Double
    ): Boolean {
        if (!validate(title, description, startDate, endDate)) return false
        viewModelScope.launch {
            val existing = repository.getTripById(id) ?: run {
                _uiState.value = TripListUiState.Error("Viaje no encontrado")
                return@launch
            }
            val updated = existing.copy(
                title = title.trim(),
                description = description.trim(),
                destination = destination.trim(),
                startDate = startDate!!,
                endDate = endDate!!,
                emoji = emoji,
                budget = budget,
                budgetSpent = budgetSpent,
                nights = endDate.compareTo(startDate).coerceAtLeast(0)
            )
            repository.updateTrip(updated)
        }
        return true
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch { repository.deleteTrip(tripId) }
    }

    private fun validate(title: String, description: String, startDate: LocalDate?, endDate: LocalDate?): Boolean {
        val errors = mutableMapOf<String, String>()
        if (title.isBlank()) errors["title"] = "El título no puede estar vacío"
        if (description.isBlank()) errors["description"] = "La descripción no puede estar vacía"
        if (startDate == null) errors["startDate"] = "Selecciona una fecha de inicio"
        if (endDate == null) errors["endDate"] = "Selecciona una fecha de fin"
        if (startDate != null && endDate != null && !startDate.isBefore(endDate))
            errors["endDate"] = "La fecha de fin debe ser posterior a la de inicio"
        _validationErrors.value = errors
        return errors.isEmpty()
    }

    fun clearValidationErrors() {
        _validationErrors.value = emptyMap()
    }
}
