package com.travelplanner.irida

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.travelplanner.irida.domain.HotelRepository
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import com.travelplanner.irida.ui.viewmodels.ReservationsViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var tripRepository: TripRepository
    private lateinit var hotelRepository: HotelRepository
    private lateinit var auth: FirebaseAuth

    private val tripConReserva = Trip(
        id = "trip-1",
        title = "Hotel Ramblas",
        description = "Reserva en Barcelona",
        startDate = LocalDate.of(2026, 5, 22),
        endDate = LocalDate.of(2026, 5, 24),
        destination = "La Rambla 33, Barcelona",
        nights = 2,
        budget = 240.0,
        emoji = "🏨",
        reservationId = "RES-001",
        hotelId = "BCN01",
        roomId = "R1",
        reservationPrice = 240.0,
        reservationStart = "2026-05-22",
        reservationEnd = "2026-05-24",
        reservationGuestName = "Iker",
        reservationGuestEmail = "iker@test.com"
    )

    private val tripSinReserva = Trip(
        id = "trip-2",
        title = "Viaje normal",
        description = "Sin reserva",
        startDate = LocalDate.of(2026, 6, 1),
        endDate = LocalDate.of(2026, 6, 5),
        destination = "Madrid",
        nights = 4,
        budget = 0.0,
        emoji = "✈️"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tripRepository  = mockk()
        hotelRepository = mockk()
        auth            = mockk()

        val mockUser = mockk<FirebaseUser> { every { uid } returns "test-uid" }
        every { auth.currentUser } returns mockUser

        // Captura el listener y dispara onAuthStateChanged inmediatamente
        val listenerSlot = slot<FirebaseAuth.AuthStateListener>()
        every { auth.addAuthStateListener(capture(listenerSlot)) } answers {
            listenerSlot.captured.onAuthStateChanged(auth)
        }
        every { auth.removeAuthStateListener(any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ReservationsViewModel(tripRepository, hotelRepository, auth)

    @Test
    fun `uiState filtra solo trips con reservationId no nulo`() = runTest {
        // Arrange
        every { tripRepository.getTripsStream("test-uid") } returns
            flowOf(listOf(tripConReserva, tripSinReserva))

        // Act
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is ReservationsViewModel.UiState.Success)
        val reservations = (state as ReservationsViewModel.UiState.Success).reservations
        assertEquals(1, reservations.size)
        assertEquals("trip-1", reservations.first().id)
    }

    @Test
    fun `cancel emite Success cuando la API funciona`() = runTest {
        // Arrange
        every { tripRepository.getTripsStream(any()) } returns flowOf(listOf(tripConReserva))
        coEvery {
            hotelRepository.cancel(any(), any(), any(), any(), any(), any())
        } returns "Reserva cancelada"
        coEvery { tripRepository.updateTrip(any()) } just Runs

        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Act
        viewModel.cancel(tripConReserva)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.cancelState.value is ReservationsViewModel.CancelState.Success)
        coVerify {
            tripRepository.updateTrip(match { it.reservationId == null && it.hotelId == null })
        }
    }

    @Test
    fun `cancel emite Error pero igualmente limpia Room cuando la API falla 500`() = runTest {
        // Arrange — comportamiento best-effort: aunque la API falle, la reserva local debe limpiarse
        every { tripRepository.getTripsStream(any()) } returns flowOf(listOf(tripConReserva))
        coEvery {
            hotelRepository.cancel(any(), any(), any(), any(), any(), any())
        } throws RuntimeException("HTTP 500 Internal Server Error")
        coEvery { tripRepository.updateTrip(any()) } just Runs

        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Act
        viewModel.cancel(tripConReserva)
        advanceUntilIdle()

        // Assert
        val state = viewModel.cancelState.value
        assertTrue(state is ReservationsViewModel.CancelState.Error)
        assertTrue((state as ReservationsViewModel.CancelState.Error).message.contains("servidor no disponible"))
        coVerify { tripRepository.updateTrip(any()) }
    }

    @Test
    fun `cancel no hace nada si el trip no tiene hotelId`() = runTest {
        // Arrange
        every { tripRepository.getTripsStream(any()) } returns flowOf(emptyList())
        val tripSinHotelId = tripConReserva.copy(hotelId = null)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Act
        viewModel.cancel(tripSinHotelId)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.cancelState.value is ReservationsViewModel.CancelState.Idle)
        coVerify(exactly = 0) {
            hotelRepository.cancel(any(), any(), any(), any(), any(), any())
        }
        coVerify(exactly = 0) { tripRepository.updateTrip(any()) }
    }
}
