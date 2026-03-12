package com.travelplanner.irida.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import com.travelplanner.irida.data.repository.TripRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.UUID

/**
 * Estados posibles de la pantalla de lista de viajes.
 */
sealed class TripListUiState {
    object Loading : TripListUiState()
    data class Success(val trips: List<Trip>) : TripListUiState()
    data class Error(val message: String) : TripListUiState()
}

/**
 * ViewModel para la pantalla HomeScreen (lista de viajes).
 *
 * Responsabilidades (T1.6):
 * - Exponer la lista de viajes como StateFlow para que la UI reaccione
 * - Implementar addTrip, editTrip, deleteTrip
 * - Validar los datos antes de enviarlos al repositorio (T1.8)
 * - Emitir errores de validación para que la UI los muestre (T3.1)
 *
 * Arquitectura: UI → TripListViewModel → TripRepository → FakeTripDataSource
 */
class TripListViewModel(
    private val repository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    private val TAG = "TripListViewModel"

    // Estado principal: lista de viajes
    private val _uiState = MutableStateFlow<TripListUiState>(TripListUiState.Loading)
    val uiState: StateFlow<TripListUiState> = _uiState.asStateFlow()

    // Errores de validación por campo — la UI los muestra debajo de cada campo
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    init {
        loadTrips()
    }

    // ── Carga ──────────────────────────────────────────────────────────────

    fun loadTrips() {
        Log.d(TAG, "loadTrips: cargando viajes del repositorio")
        val trips = repository.getTrips()
        _uiState.value = TripListUiState.Success(trips)
        Log.i(TAG, "loadTrips: ${trips.size} viajes cargados")
    }

    // ── CRUD ───────────────────────────────────────────────────────────────

    /**
     * Añade un nuevo viaje tras validar los campos.
     * Devuelve true si se añadió correctamente, false si hay errores de validación.
     */
    fun addTrip(
        title: String,
        description: String,
        destination: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        emoji: String = "✈️",
        budget: Double = 0.0
    ): Boolean {
        Log.d(TAG, "addTrip: validando datos para nuevo viaje '$title'")

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

        repository.addTrip(trip)
        loadTrips()
        Log.i(TAG, "addTrip: viaje '${trip.title}' añadido correctamente (id=${trip.id})")
        return true
    }

    /**
     * Edita un viaje existente tras validar los campos.
     * Devuelve true si se editó correctamente, false si hay errores de validación.
     */
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
        Log.d(TAG, "editTrip: validando datos para viaje id=$id")

        if (!validate(title, description, startDate, endDate)) return false

        val existing = repository.getTripById(id)
        if (existing == null) {
            Log.e(TAG, "editTrip: viaje con id=$id no encontrado")
            _uiState.value = TripListUiState.Error("Viaje no encontrado")
            return false
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
        loadTrips()
        Log.i(TAG, "editTrip: viaje '${updated.title}' actualizado correctamente")
        return true
    }

    /**
     * Elimina un viaje y todas sus actividades.
     */
    fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip: eliminando viaje id=$tripId")
        repository.deleteTrip(tripId)
        loadTrips()
        Log.i(TAG, "deleteTrip: viaje id=$tripId eliminado")
    }

    // ── Validación ─────────────────────────────────────────────────────────

    /**
     * Valida los campos del formulario de viaje.
     * Emite los errores en [validationErrors] para que la UI los muestre.
     * Devuelve true si todos los campos son válidos.
     */
    private fun validate(
        title: String,
        description: String,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Boolean {
        val errors = mutableMapOf<String, String>()

        if (title.isBlank()) {
            errors["title"] = "El título no puede estar vacío"
            Log.e(TAG, "validate: título vacío")
        }
        if (description.isBlank()) {
            errors["description"] = "La descripción no puede estar vacía"
            Log.e(TAG, "validate: descripción vacía")
        }
        if (startDate == null) {
            errors["startDate"] = "Selecciona una fecha de inicio"
            Log.e(TAG, "validate: fecha de inicio no seleccionada")
        }
        if (endDate == null) {
            errors["endDate"] = "Selecciona una fecha de fin"
            Log.e(TAG, "validate: fecha de fin no seleccionada")
        }
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            errors["endDate"] = "La fecha de fin debe ser posterior a la de inicio"
            Log.e(TAG, "validate: startDate ($startDate) no es anterior a endDate ($endDate)")
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
}