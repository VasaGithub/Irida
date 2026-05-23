package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.travelplanner.irida.domain.HotelRepository
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val hotelRepository: HotelRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val reservations: List<Trip>) : UiState
        data class Error(val message: String) : UiState
    }

    sealed interface CancelState {
        data object Idle : CancelState
        data object Loading : CancelState
        data object Success : CancelState
        data class Error(val message: String) : CancelState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _cancelState = MutableStateFlow<CancelState>(CancelState.Idle)
    val cancelState: StateFlow<CancelState> = _cancelState.asStateFlow()

    private val authUidFlow = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid ?: "") }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    init {
        viewModelScope.launch {
            authUidFlow
                .filter { it.isNotEmpty() }
                .flatMapLatest { uid -> tripRepository.getTripsStream(uid) }
                .collect { trips ->
                    _uiState.value = UiState.Success(trips.filter { it.reservationId != null })
                }
        }
    }

    fun cancel(trip: Trip) {
        val hotelId    = trip.hotelId                ?: return
        val roomId     = trip.roomId                 ?: return
        val startDate  = trip.reservationStart       ?: return
        val endDate    = trip.reservationEnd         ?: return
        val guestEmail = trip.reservationGuestEmail  ?: auth.currentUser?.email ?: return
        val guestName  = trip.reservationGuestName   ?: guestEmail
        viewModelScope.launch {
            _cancelState.value = CancelState.Loading
            val apiError = runCatching {
                hotelRepository.cancel(
                    hotelId    = hotelId,
                    roomId     = roomId,
                    startDate  = startDate,
                    endDate    = endDate,
                    guestName  = guestName,
                    guestEmail = guestEmail
                )
            }.exceptionOrNull()
            try {
                tripRepository.updateTrip(
                    trip.copy(
                        reservationId         = null,
                        hotelId               = null,
                        roomId                = null,
                        reservationPrice      = null,
                        reservationStart      = null,
                        reservationEnd        = null,
                        reservationGuestName  = null,
                        reservationGuestEmail = null
                    )
                )
                _cancelState.value = if (apiError != null) {
                    CancelState.Error(
                        "Reserva eliminada localmente (servidor no disponible: ${apiError.message})"
                    )
                } else {
                    CancelState.Success
                }
            } catch (e: Exception) {
                _cancelState.value = CancelState.Error(e.message ?: "Error al cancelar")
            }
        }
    }

    fun resetCancelState() { _cancelState.value = CancelState.Idle }
}
