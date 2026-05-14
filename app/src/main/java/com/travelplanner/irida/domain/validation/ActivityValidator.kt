package com.travelplanner.irida.domain.validation

import com.travelplanner.irida.domain.Trip
import java.time.LocalDate
import java.time.LocalTime

object ActivityValidator {
    fun validate(
        title: String,
        description: String,
        date: LocalDate?,
        time: LocalTime?,
        trip: Trip
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (title.isBlank()) errors["title"] = "El título no puede estar vacío"
        if (description.isBlank()) errors["description"] = "La descripción no puede estar vacía"
        if (date == null) {
            errors["date"] = "Selecciona una fecha para la actividad"
        } else if (!trip.isDateInRange(date)) {
            errors["date"] = "La fecha debe estar entre ${trip.startDate} y ${trip.endDate}"
        }
        if (time == null) errors["time"] = "Selecciona una hora para la actividad"
        return errors
    }
}
