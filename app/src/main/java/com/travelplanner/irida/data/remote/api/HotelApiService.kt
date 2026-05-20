package com.travelplanner.irida.data.remote.api

import com.travelplanner.irida.data.remote.dto.ApiMessageDto
import com.travelplanner.irida.data.remote.dto.AvailabilityResponseDto
import com.travelplanner.irida.data.remote.dto.HotelDto
import com.travelplanner.irida.data.remote.dto.ReservationDto
import com.travelplanner.irida.data.remote.dto.ReservationResponseDto
import com.travelplanner.irida.data.remote.dto.ReserveRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String
    ): List<HotelDto>

    @GET("hotels/{group_id}/availability")
    suspend fun getAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hotel_id") hotelId: String? = null,
        @Query("city") city: String? = null
    ): AvailabilityResponseDto

    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequestDto
    ): ReservationResponseDto

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body request: Map<String, String>
    ): ApiMessageDto

    @GET("hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): Map<String, List<ReservationDto>>
}
