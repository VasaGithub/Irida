package com.travelplanner.irida.domain

import java.time.LocalDate

data class Trip(
    val id: String,
    val title: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destination: String = "",
    val nights: Int = 0,
    val budget: Double = 0.0,
    val budgetSpent: Double = 0.0,
    val emoji: String = "✈️",
    val activities: List<Activity> = emptyList()
) {
    /** Calcula el presupuesto restante. */
    fun getRemainingBudget(): Double = budget - budgetSpent

    /** Porcentaje de presupuesto gastado. */
    fun getBudgetProgressPercent(): Int =
        if (budget > 0) ((budgetSpent / budget) * 100).toInt() else 0

    /**
     * Noches calculadas desde las fechas.
     * Usar este método es preferible al campo nights, que puede quedar
     * desincronizado si se editan las fechas.
     */
    fun getNights(): Long =
        java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)

    /**
     * Comprueba si una fecha está dentro del rango del viaje (inclusive).
     * Usado para validar que las actividades no estén fuera del viaje (T1.3).
     */
    fun isDateInRange(date: LocalDate): Boolean =
        !date.isBefore(startDate) && !date.isAfter(endDate)
}