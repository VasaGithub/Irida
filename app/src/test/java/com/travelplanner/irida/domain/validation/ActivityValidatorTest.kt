package com.travelplanner.irida.domain.validation

import com.travelplanner.irida.domain.Trip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ActivityValidatorTest {

    private val tripStart = LocalDate.of(2026, 5, 10)
    private val tripEnd = LocalDate.of(2026, 5, 20)
    private lateinit var trip: Trip

    @Before
    fun setUp() {
        trip = Trip(
            id = "trip-1",
            title = "Tokio",
            description = "Desc",
            startDate = tripStart,
            endDate = tripEnd
        )
    }

    @Test
    fun `actividad valida devuelve mapa vacio`() {
        val errors = ActivityValidator.validate(
            "Visita museo", "Entrada a las 10",
            LocalDate.of(2026, 5, 15), LocalTime.of(10, 0), trip
        )
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `titulo vacio produce error en title`() {
        val errors = ActivityValidator.validate(
            "", "desc", LocalDate.of(2026, 5, 15), LocalTime.of(10, 0), trip
        )
        assertEquals("El título no puede estar vacío", errors["title"])
    }

    @Test
    fun `descripcion vacia produce error en description`() {
        val errors = ActivityValidator.validate(
            "Visita", "  ", LocalDate.of(2026, 5, 15), LocalTime.of(10, 0), trip
        )
        assertEquals("La descripción no puede estar vacía", errors["description"])
    }

    @Test
    fun `date null produce error en date`() {
        val errors = ActivityValidator.validate(
            "Visita", "desc", null, LocalTime.of(10, 0), trip
        )
        assertEquals("Selecciona una fecha para la actividad", errors["date"])
    }

    @Test
    fun `date fuera del rango del trip produce error en date`() {
        val errors = ActivityValidator.validate(
            "Visita", "desc", LocalDate.of(2026, 6, 1), LocalTime.of(10, 0), trip
        )
        assertEquals(
            "La fecha debe estar entre ${trip.startDate} y ${trip.endDate}",
            errors["date"]
        )
    }

    @Test
    fun `date antes del inicio del trip produce error en date`() {
        val errors = ActivityValidator.validate(
            "Visita", "desc", LocalDate.of(2026, 5, 9), LocalTime.of(10, 0), trip
        )
        assertEquals(
            "La fecha debe estar entre ${trip.startDate} y ${trip.endDate}",
            errors["date"]
        )
    }

    @Test
    fun `time null produce error en time`() {
        val errors = ActivityValidator.validate(
            "Visita", "desc", LocalDate.of(2026, 5, 15), null, trip
        )
        assertEquals("Selecciona una hora para la actividad", errors["time"])
    }
}
