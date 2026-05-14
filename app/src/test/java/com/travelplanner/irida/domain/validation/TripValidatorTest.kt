package com.travelplanner.irida.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TripValidatorTest {

    private val today = LocalDate.of(2026, 5, 14)
    private val tomorrow = today.plusDays(1)

    @Test
    fun `viaje valido devuelve mapa vacio`() {
        val errors = TripValidator.validate("Tokio", "Viaje de prueba", today, tomorrow)
        assertTrue("Sin errores", errors.isEmpty())
    }

    @Test
    fun `titulo vacio produce error en title`() {
        val errors = TripValidator.validate("  ", "desc", today, tomorrow)
        assertEquals("El título no puede estar vacío", errors["title"])
    }

    @Test
    fun `descripcion vacia produce error en description`() {
        val errors = TripValidator.validate("Tokio", "", today, tomorrow)
        assertEquals("La descripción no puede estar vacía", errors["description"])
    }

    @Test
    fun `startDate null produce error en startDate`() {
        val errors = TripValidator.validate("Tokio", "desc", null, tomorrow)
        assertEquals("Selecciona una fecha de inicio", errors["startDate"])
    }

    @Test
    fun `endDate null produce error en endDate`() {
        val errors = TripValidator.validate("Tokio", "desc", today, null)
        assertEquals("Selecciona una fecha de fin", errors["endDate"])
    }

    @Test
    fun `endDate no posterior a startDate produce error en endDate`() {
        val errors = TripValidator.validate("Tokio", "desc", tomorrow, today)
        assertEquals("La fecha de fin debe ser posterior a la de inicio", errors["endDate"])
    }

    @Test
    fun `endDate igual a startDate produce error en endDate`() {
        val errors = TripValidator.validate("Tokio", "desc", today, today)
        assertEquals("La fecha de fin debe ser posterior a la de inicio", errors["endDate"])
    }

    @Test
    fun `multiples campos invalidos acumulan errores`() {
        val errors = TripValidator.validate("", "", null, null)
        assertEquals(4, errors.size)
        assertFalse(errors["title"].isNullOrEmpty())
        assertFalse(errors["description"].isNullOrEmpty())
        assertFalse(errors["startDate"].isNullOrEmpty())
        assertFalse(errors["endDate"].isNullOrEmpty())
        assertNull(errors["other"])
    }
}
