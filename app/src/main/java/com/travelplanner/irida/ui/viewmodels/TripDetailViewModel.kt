package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

sealed class TripDetailUiState {
    object Loading : TripDetailUiState()
    data class Success(val trip: Trip, val activities: List<Activity>) : TripDetailUiState()
    data class Error(val message: String) : TripDetailUiState()
}

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val repository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripDetailUiState>(TripDetailUiState.Loading)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private var currentTripId: String = ""
    private var collectJob: Job? = null

    fun loadTrip(tripId: String) {
        currentTripId = tripId
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            val trip = repository.getTripById(tripId)
            if (trip == null) {
                _uiState.value = TripDetailUiState.Error("Viaje no encontrado")
                return@launch
            }
            repository.getActivitiesStream(tripId).collect { activities ->
                _uiState.value = TripDetailUiState.Success(
                    trip,
                    activities.sortedWith(compareBy({ it.date }, { it.time }))
                )
            }
        }
    }

    fun addActivity(title: String, description: String, date: LocalDate?, time: LocalTime?): Boolean {
        val trip = getCurrentTrip() ?: return false
        if (!validateActivity(title, description, date, time, trip)) return false
        val activity = Activity(
            id = UUID.randomUUID().toString(),
            tripId = currentTripId,
            title = title.trim(),
            description = description.trim(),
            date = date!!,
            time = time!!
        )
        viewModelScope.launch { repository.addActivity(activity) }
        return true
    }

    fun updateActivity(activityId: String, title: String, description: String, date: LocalDate?, time: LocalTime?): Boolean {
        val trip = getCurrentTrip() ?: return false
        if (!validateActivity(title, description, date, time, trip)) return false
        val updated = Activity(
            id = activityId,
            tripId = currentTripId,
            title = title.trim(),
            description = description.trim(),
            date = date!!,
            time = time!!
        )
        viewModelScope.launch { repository.updateActivity(updated) }
        return true
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch { repository.deleteActivity(activityId) }
    }

    private fun validateActivity(title: String, description: String, date: LocalDate?, time: LocalTime?, trip: Trip): Boolean {
        val errors = mutableMapOf<String, String>()
        if (title.isBlank()) errors["title"] = "El título no puede estar vacío"
        if (description.isBlank()) errors["description"] = "La descripción no puede estar vacía"
        if (date == null) {
            errors["date"] = "Selecciona una fecha para la actividad"
        } else if (!trip.isDateInRange(date)) {
            errors["date"] = "La fecha debe estar entre ${trip.startDate} y ${trip.endDate}"
        }
        if (time == null) errors["time"] = "Selecciona una hora para la actividad"
        _validationErrors.value = errors
        return errors.isEmpty()
    }

    fun clearValidationErrors() {
        _validationErrors.value = emptyMap()
    }

    private fun getCurrentTrip(): Trip? =
        (_uiState.value as? TripDetailUiState.Success)?.trip
}
