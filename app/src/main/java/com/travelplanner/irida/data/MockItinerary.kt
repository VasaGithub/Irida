package com.travelplanner.irida.data

import com.travelplanner.irida.domain.ItineraryItem
import com.travelplanner.irida.domain.Trip

val mockItinerary = listOf(
    ItineraryItem(id = "1", time = "08:00", title = "Vuelo BCN → NRT", description = "Vueling VY7182 · Terminal 1", location = "Aeropuerto Barcelona", cost = 420.0, emoji = "✈️", isBooked = true),
    ItineraryItem(id = "2", time = "22:30", title = "Check-in · Hotel Shinjuku", description = "Shinjuku, Tokio · 4★", location = "Shinjuku, Tokio", cost = 95.0, emoji = "🏨", isBooked = true),
    ItineraryItem(id = "3", time = "09:00", title = "Templo Senso-ji", description = "Asakusa · 2h visita", location = "Asakusa, Tokyo", cost = 0.0, emoji = "⛩️", isBooked = false),
    ItineraryItem(id = "4", time = "13:00", title = "Ramen Ippudo", description = "Shibuya · Reserva hecha", location = "Shibuya, Tokio", cost = 18.0, emoji = "🍜", isBooked = true),
    ItineraryItem(id = "5", time = "15:30", title = "Cruce de Shibuya", description = "Cruce icónico · 1h", location = "Shibuya, Tokyo", cost = 0.0, emoji = "🏙️", isBooked = false),
    ItineraryItem(id = "6", time = "20:00", title = "Sushi Saito", description = "Restaurante omakase · Reserva obligatoria", location = "Roppongi, Tokio", cost = 85.0, emoji = "🍣", isBooked = true)
)

val mockTripTokyo = Trip(
    id = "1",
    title = "Aventura en Tokio",
    destination = "Tokio, Japón",
    startDate = "Mar 10",
    endDate = "Mar 18",
    nights = 8,
    budget = 1240.0,
    budgetSpent = 806.0,
    emoji = "🗼",
    activities = mockItinerary
)