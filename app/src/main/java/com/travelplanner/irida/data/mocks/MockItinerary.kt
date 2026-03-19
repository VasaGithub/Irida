package com.travelplanner.irida.data.mocks

import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import java.time.LocalDate
import java.time.LocalTime

/**
 * Mock de actividades para previews y desarrollo.
 * Usa el modelo Activity del Sprint 02 (LocalDate + LocalTime).
 *
 * ItineraryItem (Sprint 01) queda deprecado — estas actividades lo reemplazan
 * en las pantallas que ya consumen el nuevo modelo.
 */
val mockActivitiesTokyo = listOf(
    Activity(
        id = "act-1",
        tripId = "1",
        title = "Vuelo BCN → NRT",
        description = "Vueling VY7182 · Terminal 1",
        date = LocalDate.of(2026, 3, 10),
        time = LocalTime.of(8, 0)
    ),
    Activity(
        id = "act-2",
        tripId = "1",
        title = "Check-in Hotel Shinjuku",
        description = "Shinjuku, Tokio · 4★",
        date = LocalDate.of(2026, 3, 10),
        time = LocalTime.of(22, 30)
    ),
    Activity(
        id = "act-3",
        tripId = "1",
        title = "Templo Senso-ji",
        description = "Asakusa · 2h visita",
        date = LocalDate.of(2026, 3, 11),
        time = LocalTime.of(9, 0)
    ),
    Activity(
        id = "act-4",
        tripId = "1",
        title = "Ramen Ippudo",
        description = "Shibuya · Reserva hecha",
        date = LocalDate.of(2026, 3, 11),
        time = LocalTime.of(13, 0)
    ),
    Activity(
        id = "act-5",
        tripId = "1",
        title = "Cruce de Shibuya",
        description = "Cruce icónico · 1h",
        date = LocalDate.of(2026, 3, 11),
        time = LocalTime.of(15, 30)
    ),
    Activity(
        id = "act-6",
        tripId = "1",
        title = "Sushi Saito",
        description = "Restaurante omakase · Reserva obligatoria",
        date = LocalDate.of(2026, 3, 12),
        time = LocalTime.of(20, 0)
    )
)

val mockTripTokyo = Trip(
    id = "1",
    title = "Aventura en Tokio",
    description = "Explorando la cultura japonesa: templos, gastronomía y tecnología.",
    destination = "Tokio, Japón",
    startDate = LocalDate.of(2026, 3, 10),
    endDate = LocalDate.of(2026, 3, 18),
    nights = 8,
    budget = 1240.0,
    budgetSpent = 806.0,
    emoji = "🗼",
    activities = mockActivitiesTokyo
)