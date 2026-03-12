package com.travelplanner.irida.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import com.travelplanner.irida.data.repository.TripRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Estados posibles de la pantalla de detalle de un viaje.
 */
sealed class TripDetailUiState {
    object Loading : TripDetailUiState()
    data class Success(
        val trip: Trip,
        val activities: List<Activity>
    ) : TripDetailUiState()
    data class Error(val message: String) : TripDetailUiState()
}

/**
 * ViewModel para TripDetailScreen e ItineraryScreen.
 *
 * Responsabilidades (T1.7):
 * - Cargar y exponer el viaje seleccionado y sus actividades como StateFlow
 * - Implementar addActivity, updateActivity, deleteActivity
 * - Validar los datos de actividad antes de enviarlos al repositorio (T1.8)
 * - Validar que la actividad esté dentro del rango de fechas del viaje (T1.3)
 * - Emitir errores de validación para que la UI los muestre (T3.1)
 *
 * Arquitectura: UI → TripDetailViewModel → TripRepository → FakeTripDataSource
 */
class TripDetailViewModel(
    private val repository: TripRepository = TripRepositoryImpl.instance
) : ViewModel() {

    private val TAG = "TripDetailViewModel"

    // Estado principal: viaje actual + sus actividades
    private val _uiState = MutableStateFlow<TripDetailUiState>(TripDetailUiState.Loading)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    // Errores de validación por campo
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    // ID del viaje actualmente cargado
    private var currentTripId: String = ""

    // ── Carga ──────────────────────────────────────────────────────────────

    /**
     * Carga el viaje con [tripId] y sus actividades.
     * Debe llamarse desde la UI al navegar a TripDetailScreen.
     */
    fun loadTrip(tripId: String) {
        Log.d(TAG, "loadTrip: cargando viaje id=$tripId")
        currentTripId = tripId

        val trip = repository.getTripById(tripId)
        if (trip == null) {
            Log.e(TAG, "loadTrip: viaje con id=$tripId no encontrado")
            _uiState.value = TripDetailUiState.Error("Viaje no encontrado")
            return
        }

        val activities = repository.getActivities(tripId)
            .sortedWith(compareBy({ it.date }, { it.time }))

        _uiState.value = TripDetailUiState.Success(trip, activities)
        Log.i(TAG, "loadTrip: viaje '${trip.title}' cargado con ${activities.size} actividades")
    }

    // ── CRUD de actividades ────────────────────────────────────────────────

    /**
     * Añade una nueva actividad al viaje actual tras validar los campos.
     * Devuelve true si se añadió correctamente, false si hay errores.
     */
    fun addActivity(
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?
    ): Boolean {
        Log.d(TAG, "addActivity: validando datos para nueva actividad '$title'")

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

        repository.addActivity(activity)
        loadTrip(currentTripId)
        Log.i(TAG, "addActivity: actividad '${activity.title}' añadida (id=${activity.id})")
        return true
    }

    /**
     * Actualiza una actividad existente tras validar los campos.
     * Devuelve true si se actualizó correctamente, false si hay errores.
     */
    fun updateActivity(
        activityId: String,
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?
    ): Boolean {
        Log.d(TAG, "updateActivity: validando datos para actividad id=$activityId")

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

        repository.updateActivity(updated)
        loadTrip(currentTripId)
        Log.i(TAG, "updateActivity: actividad '${updated.title}' actualizada")
        return true
    }

    /**
     * Elimina una actividad por su ID.
     */
    fun deleteActivity(activityId: String) {
        Log.d(TAG, "deleteActivity: eliminando actividad id=$activityId")
        repository.deleteActivity(activityId)
        loadTrip(currentTripId)
        Log.i(TAG, "deleteActivity: actividad id=$activityId eliminada")
    }

    // ── Validación ─────────────────────────────────────────────────────────

    /**
     * Valida los campos del formulario de actividad.
     * Comprueba también que la fecha esté dentro del rango del viaje (T1.3).
     * Emite errores en [validationErrors] para que la UI los muestre.
     */
    private fun validateActivity(
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?,
        trip: Trip
    ): Boolean {
        val errors = mutableMapOf<String, String>()

        if (title.isBlank()) {
            errors["title"] = "El título no puede estar vacío"
            Log.e(TAG, "validateActivity: título vacío")
        }
        if (description.isBlank()) {
            errors["description"] = "La descripción no puede estar vacía"
            Log.e(TAG, "validateActivity: descripción vacía")
        }
        if (date == null) {
            errors["date"] = "Selecciona una fecha para la actividad"
            Log.e(TAG, "validateActivity: fecha no seleccionada")
        } else if (!trip.isDateInRange(date)) {
            // Validación clave T1.3: la actividad debe estar dentro del rango del viaje
            errors["date"] = "La fecha debe estar entre ${trip.startDate} y ${trip.endDate}"
            Log.e(TAG, "validateActivity: fecha $date fuera del rango del viaje [${trip.startDate}, ${trip.endDate}]")
        }
        if (time == null) {
            errors["time"] = "Selecciona una hora para la actividad"
            Log.e(TAG, "validateActivity: hora no seleccionada")
        }

        _validationErrors.value = errors
        return errors.isEmpty()
    }

    /**
     * Limpia los errores de validación — llamar al abrir el formulario.
     */
    fun clearValidationErrors() {
        _validationErrors.value = emptyMap()
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun getCurrentTrip(): Trip? {
        val state = _uiState.value
        if (state !is TripDetailUiState.Success) {
            Log.e(TAG, "getCurrentTrip: estado no es Success, no se puede operar")
            return null
        }
        return state.trip
    }
}