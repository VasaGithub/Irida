package com.travelplanner.irida.data.repository

import android.util.Log
import com.travelplanner.irida.BuildConfig
import com.travelplanner.irida.data.remote.api.HotelApiService
import com.travelplanner.irida.data.remote.dto.CancelRequestDto
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

    companion object {
        private const val TAG = "HotelRepository"
    }

    override suspend fun getHotels(): List<Hotel> {
        Log.d(TAG, "getHotels() → groupId=$groupId")
        return try {
            val result = api.getHotels(groupId).map { it.toDomain() }
            Log.i(TAG, "getHotels() ← ${result.size} hoteles recibidos")
            result
        } catch (e: Exception) {
            Log.e(TAG, "getHotels() error: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getAvailability(
        start: LocalDate,
        end: LocalDate,
        city: String?,
        hotelId: String?
    ): List<Hotel> {
        Log.d(TAG, "getAvailability() → start=$start end=$end city=$city hotelId=$hotelId")
        return try {
            val result = api.getAvailability(
                groupId   = groupId,
                startDate = start.format(dateFormatter),
                endDate   = end.format(dateFormatter),
                city      = city,
                hotelId   = hotelId
            ).available_hotels.map { it.toDomain() }
            Log.i(TAG, "getAvailability() ← ${result.size} hoteles disponibles")
            result
        } catch (e: Exception) {
            Log.e(TAG, "getAvailability() error: ${e.message}", e)
            throw e
        }
    }

    override suspend fun reserve(
        hotelId: String,
        roomId: String,
        start: LocalDate,
        end: LocalDate,
        guestName: String,
        guestEmail: String
    ): Reservation {
        Log.d(TAG, "reserve() → hotelId=$hotelId roomId=$roomId guest=$guestEmail start=$start end=$end")
        return try {
            val reservation = api.reserveRoom(
                groupId = groupId,
                request = ReserveRequestDto(
                    hotel_id   = hotelId,
                    room_id    = roomId,
                    start_date = start.format(dateFormatter),
                    end_date   = end.format(dateFormatter),
                    guest_name = guestName,
                    guest_email = guestEmail
                )
            ).reservation.toDomain()
            Log.i(TAG, "reserve() ← reserva creada id=${reservation.id}")
            reservation
        } catch (e: Exception) {
            Log.e(TAG, "reserve() error: ${e.message}", e)
            throw e
        }
    }

    override suspend fun cancel(
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): String {
        Log.d(TAG, "cancel() → hotelId=$hotelId roomId=$roomId guest=$guestEmail")
        return try {
            val msg = api.cancelReservation(
                groupId = groupId,
                request = CancelRequestDto(
                    hotel_id    = hotelId,
                    room_id     = roomId,
                    start_date  = startDate,
                    end_date    = endDate,
                    guest_name  = guestName,
                    guest_email = guestEmail
                )
            ).message
            Log.i(TAG, "cancel() ← $msg")
            msg
        } catch (e: Exception) {
            Log.e(TAG, "cancel() error: ${e.message}", e)
            throw e
        }
    }

    override suspend fun listReservations(guestEmail: String?): List<Reservation> {
        Log.d(TAG, "listReservations() → guestEmail=$guestEmail")
        return try {
            val result = api.listReservations(groupId, guestEmail)
                .getOrDefault("reservations", emptyList())
                .map { it.toDomain() }
            Log.i(TAG, "listReservations() ← ${result.size} reservas")
            result
        } catch (e: Exception) {
            Log.e(TAG, "listReservations() error: ${e.message}", e)
            throw e
        }
    }
}
