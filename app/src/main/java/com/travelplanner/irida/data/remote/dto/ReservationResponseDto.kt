package com.travelplanner.irida.data.remote.dto

data class ReservationResponseDto(
    val message: String,
    val nights: Int,
    val reservation: ReservationDto
)
