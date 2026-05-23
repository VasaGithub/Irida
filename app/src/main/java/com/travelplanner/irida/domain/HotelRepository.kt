package com.travelplanner.irida.domain

import java.time.LocalDate

interface HotelRepository {

    suspend fun getHotels(): List<Hotel>

    suspend fun getAvailability(
        start: LocalDate,
        end: LocalDate,
        city: String? = null,
        hotelId: String? = null
    ): List<Hotel>

    suspend fun reserve(
        hotelId: String,
        roomId: String,
        start: LocalDate,
        end: LocalDate,
        guestName: String,
        guestEmail: String
    ): Reservation

    suspend fun cancel(
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): String

    suspend fun listReservations(guestEmail: String? = null): List<Reservation>
}
