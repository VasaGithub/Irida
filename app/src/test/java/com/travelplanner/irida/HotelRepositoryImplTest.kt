package com.travelplanner.irida

import com.travelplanner.irida.data.remote.api.HotelApiService
import com.travelplanner.irida.data.repository.HotelRepositoryImpl
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class HotelRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: HotelRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(HotelApiService::class.java)
        repository = HotelRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getAvailability devuelve hoteles mapeados cuando la API responde 200`() = runTest {
        // Arrange
        val json = javaClass.classLoader!!
            .getResourceAsStream("availability_bcn_ok.json")!!
            .bufferedReader().readText()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        // Act
        val hotels = repository.getAvailability(
            start = LocalDate.of(2026, 5, 22),
            end   = LocalDate.of(2026, 5, 24),
            city  = "BCN"
        )

        // Assert
        assertEquals(1, hotels.size)
        assertEquals("Hotel Ramblas", hotels.first().name)
        assertEquals(3, hotels.first().rooms.size)
        assertEquals("single", hotels.first().rooms.first().roomType)

        val request = server.takeRequest()
        assert(request.path!!.contains("availability"))
        assert(request.path!!.contains("city=BCN"))
    }

    @Test
    fun `reserve devuelve la reserva mapeada cuando la API responde 200`() = runTest {
        // Arrange
        val json = javaClass.classLoader!!
            .getResourceAsStream("reserve_ok.json")!!
            .bufferedReader().readText()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        // Act
        val reservation = repository.reserve(
            hotelId    = "BCN01",
            roomId     = "R1",
            start      = LocalDate.of(2026, 5, 22),
            end        = LocalDate.of(2026, 5, 24),
            guestName  = "Iker",
            guestEmail = "iker@test.com"
        )

        // Assert
        assertEquals("ABC123", reservation.id)
        assertEquals("BCN01", reservation.hotelId)
        assertEquals("R1", reservation.roomId)
        assertEquals(LocalDate.of(2026, 5, 22), reservation.startDate)

        val request = server.takeRequest()
        assert(request.body.readUtf8().contains("BCN01"))
    }

    @Test(expected = HttpException::class)
    fun `getAvailability lanza HttpException cuando la API responde 400`() = runTest {
        // Arrange
        val json = javaClass.classLoader!!
            .getResourceAsStream("error_400.json")!!
            .bufferedReader().readText()

        server.enqueue(MockResponse().setResponseCode(400).setBody(json))

        // Act — debe lanzar HttpException
        repository.getAvailability(
            start = LocalDate.of(2025, 1, 1),
            end   = LocalDate.of(2025, 1, 5),
            city  = "BCN"
        )
    }
}
