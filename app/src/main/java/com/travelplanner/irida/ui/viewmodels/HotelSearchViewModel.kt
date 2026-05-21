package com.travelplanner.irida.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelplanner.irida.domain.AuthRepository
import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.domain.HotelRepository
import com.travelplanner.irida.domain.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HotelSearchViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ── Search UI state ───────────────────────────────────────────────────
    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val hotels: List<Hotel>) : UiState
        data class Error(val message: String) : UiState
    }

    // ── Reserve UI state ──────────────────────────────────────────────────
    sealed interface ReserveUiState {
        data object Idle : ReserveUiState
        data object Loading : ReserveUiState
        data class Success(val reservation: Reservation) : ReserveUiState
        data class Error(val message: String) : ReserveUiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _reserveUiState = MutableStateFlow<ReserveUiState>(ReserveUiState.Idle)
    val reserveUiState: StateFlow<ReserveUiState> = _reserveUiState.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate.asStateFlow()

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel: StateFlow<Hotel?> = _selectedHotel.asStateFlow()

    val canSearch: StateFlow<Boolean> = combine(_startDate, _endDate, _city) { s, e, c ->
        s != null && e != null && c.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Email of the currently authenticated user, for pre-filling the reservation form. */
    val currentGuestEmail: String
        get() = authRepository.currentUser?.email ?: ""

    // ── Search ────────────────────────────────────────────────────────────
    fun onStartDateSelected(date: LocalDate) { _startDate.value = date }
    fun onEndDateSelected(date: LocalDate)   { _endDate.value   = date }
    fun onCitySelected(c: String)            { _city.value      = c    }

    fun search() {
        val start = _startDate.value ?: return
        val end   = _endDate.value   ?: return
        val c     = _city.value.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val hotels = hotelRepository.getAvailability(start, end, c)
                _uiState.value = UiState.Success(hotels)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // ── Detail / Reservation ──────────────────────────────────────────────
    fun selectHotel(hotel: Hotel) { _selectedHotel.value = hotel }

    fun reserve(roomId: String, guestName: String, guestEmail: String) {
        val hotel = _selectedHotel.value ?: return
        val start = _startDate.value    ?: return
        val end   = _endDate.value      ?: return
        viewModelScope.launch {
            _reserveUiState.value = ReserveUiState.Loading
            try {
                val reservation = hotelRepository.reserve(
                    hotelId = hotel.id,
                    roomId = roomId,
                    start = start,
                    end = end,
                    guestName = guestName,
                    guestEmail = guestEmail
                )
                _reserveUiState.value = ReserveUiState.Success(reservation)
            } catch (e: Exception) {
                _reserveUiState.value = ReserveUiState.Error(
                    e.message ?: "Error al realizar la reserva"
                )
            }
        }
    }

    fun resetReserveState() { _reserveUiState.value = ReserveUiState.Idle }
}
