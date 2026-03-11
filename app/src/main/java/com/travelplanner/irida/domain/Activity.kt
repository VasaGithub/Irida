package com.travelplanner.irida.domain

import java.time.LocalDate
import java.time.LocalTime

data class Activity(
    val id: String,
    val tripId: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime
)