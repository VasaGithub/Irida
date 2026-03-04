package com.travelplanner.irida.data

import com.travelplanner.irida.domain.Trip

val mockTrips = listOf(
    Trip(
        id = "1",
        title = "Aventura en Tokyo",
        destination = "Tokyo, Japón",
        startDate = "Mar 10",
        endDate = "Mar 18",
        nights = 8,
        budget = 1240.0,
        budgetSpent = 806.0,
        emoji = "🗼",
        activities = emptyList()
    ),
    Trip(
        id = "2",
        title = "Escapada a París",
        destination = "París, Francia",
        startDate = "Abr 5",
        endDate = "Abr 9",
        nights = 4,
        budget = 890.0,
        budgetSpent = 267.0,
        emoji = "🗽",
        activities = emptyList()
    ),
    Trip(
        id = "3",
        title = "Evasión en Bali",
        destination = "Bali, Indonesia",
        startDate = "May 20",
        endDate = "May 30",
        nights = 10,
        budget = 1500.0,
        budgetSpent = 0.0,
        emoji = "🌴",
        activities = emptyList()
    )
)