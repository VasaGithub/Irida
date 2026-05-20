package com.travelplanner.irida.domain

import java.time.LocalDate

data class Reservation(
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val guestName: String,
    val guestEmail: String,
    val hotel: Hotel? = null,
    val room: Room? = null
)
