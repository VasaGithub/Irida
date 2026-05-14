package com.travelplanner.irida.domain.validation

import java.time.LocalDate

object TripValidator {
    fun validate(
        title: String,
        description: String,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (title.isBlank()) errors["title"] = "El título no puede estar vacío"
        if (description.isBlank()) errors["description"] = "La descripción no puede estar vacía"
        if (startDate == null) errors["startDate"] = "Selecciona una fecha de inicio"
        if (endDate == null) errors["endDate"] = "Selecciona una fecha de fin"
        if (startDate != null && endDate != null && !startDate.isBefore(endDate))
            errors["endDate"] = "La fecha de fin debe ser posterior a la de inicio"
        return errors
    }
}
