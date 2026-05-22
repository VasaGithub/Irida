package com.travelplanner.irida.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String,
    val nights: Int,
    val budget: Double,
    val budgetSpent: Double,
    val emoji: String,
    val reservationId: String? = null,
    val hotelId: String? = null,
    val roomId: String? = null,
    val reservationPrice: Double? = null,
    val reservationStart: String? = null,
    val reservationEnd: String? = null,
    val reservationGuestName: String? = null,
    val reservationGuestEmail: String? = null
)
