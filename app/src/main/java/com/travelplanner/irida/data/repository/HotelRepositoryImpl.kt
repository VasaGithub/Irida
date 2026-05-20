package com.travelplanner.irida.data.repository

import com.travelplanner.irida.BuildConfig
import com.travelplanner.irida.data.remote.api.HotelApiService
import com.travelplanner.irida.data.remote.dto.ReserveRequestDto
import com.travelplanner.irida.data.remote.mapper.toDomain
import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.domain.HotelRepository
import com.travelplanner.irida.domain.Reservation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApiService
) : HotelRepository {

    private val groupId = BuildConfig.GROUP_ID
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun getHotels(): List<Hotel> =
        api.getHotels(groupId).map { it.toDomain() }

    override suspend fun getAvailability(
        start: LocalDate,
        end: LocalDate,
        city: String?,
        hotelId: String?
    ): List<Hotel> =
        api.getAvailability(
            groupId = groupId,
            startDate = start.format(dateFormatter),
            endDate = end.format(dateFormatter),
            city = city,
            hotelId = hotelId
        ).available_hotels.map { it.toDomain() }

    override suspend fun reserve(
        hotelId: String,
        roomId: String,
        start: LocalDate,
        end: LocalDate,
        guestName: String,
        guestEmail: String
    ): Reservation =
        api.reserveRoom(
            groupId = groupId,
            request = ReserveRequestDto(
                hotel_id = hotelId,
                room_id = roomId,
                start_date = start.format(dateFormatter),
                end_date = end.format(dateFormatter),
                guest_name = guestName,
                guest_email = guestEmail
            )
        ).reservation.toDomain()

    override suspend fun cancel(reservationId: String): String =
        api.cancelReservation(
            groupId = groupId,
            request = mapOf("reservation_id" to reservationId)
        ).message

    override suspend fun listReservations(guestEmail: String?): List<Reservation> =
        api.listReservations(groupId, guestEmail)
            .getOrDefault("reservations", emptyList())
            .map { it.toDomain() }
}
