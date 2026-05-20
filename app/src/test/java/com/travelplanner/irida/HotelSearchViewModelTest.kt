package com.travelplanner.irida

import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.domain.HotelRepository
import com.travelplanner.irida.ui.viewmodels.HotelSearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HotelSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var hotelRepository: HotelRepository
    private lateinit var viewModel: HotelSearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hotelRepository = mockk()
        viewModel = HotelSearchViewModel(hotelRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search actualiza el estado a Success con la lista de hoteles`() = runTest {
        // Arrange
        val hotelesFake = listOf(
            Hotel(id = "BCN01", name = "Hotel Ramblas", address = "La Rambla 33", rating = 4, imageUrl = "/images/BCN01.png")
        )
        coEvery {
            hotelRepository.getAvailability(any(), any(), any(), any())
        } returns hotelesFake

        // Act
        viewModel.search(
            start = LocalDate.of(2026, 5, 22),
            end   = LocalDate.of(2026, 5, 24),
            city  = "BCN"
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is HotelSearchViewModel.UiState.Success)
        assertEquals(1, (state as HotelSearchViewModel.UiState.Success).hotels.size)
        assertEquals("Hotel Ramblas", state.hotels.first().name)
    }

    @Test
    fun `search actualiza el estado a Error cuando el repositorio lanza excepcion`() = runTest {
        // Arrange
        coEvery {
            hotelRepository.getAvailability(any(), any(), any(), any())
        } throws RuntimeException("Sin conexion")

        // Act
        viewModel.search(
            start = LocalDate.of(2026, 5, 22),
            end   = LocalDate.of(2026, 5, 24),
            city  = "BCN"
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is HotelSearchViewModel.UiState.Error)
        assertEquals("Sin conexion", (state as HotelSearchViewModel.UiState.Error).message)
    }
}
