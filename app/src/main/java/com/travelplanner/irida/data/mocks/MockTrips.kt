package com.travelplanner.irida.data.mocks

import com.travelplanner.irida.domain.Trip
import java.time.LocalDate

val mockTrips = listOf(
    Trip(
        id = "1",
        title = "Aventura en Tokyo",
        description = "Descubriendo la cultura y tecnología de Tokyo",
        destination = "Tokyo, Japón",
        startDate = LocalDate.of(2026, 3, 10),
        endDate = LocalDate.of(2026, 3, 18),
        nights = 8,
        budget = 1240.0,
        budgetSpent = 806.0,
        emoji = "🗼",
        activities = emptyList()
    ),
    Trip(
        id = "2",
        title = "Escapada a París",
        description = "Unos días disfrutando de la ciudad del amor",
        destination = "París, Francia",
        startDate = LocalDate.of(2026, 4, 5),
        endDate = LocalDate.of(2026, 4, 9),
        nights = 4,
        budget = 890.0,
        budgetSpent = 267.0,
        emoji = "🗽",
        activities = emptyList()
    ),
    Trip(
        id = "3",
        title = "Evasión en Bali",
        description = "Relajarse en playas paradisíacas y naturaleza",
        destination = "Bali, Indonesia",
        startDate = LocalDate.of(2026, 5, 20),
        endDate = LocalDate.of(2026, 5, 30),
        nights = 10,
        budget = 1500.0,
        budgetSpent = 0.0,
        emoji = "🌴",
        activities = emptyList()
    )
)