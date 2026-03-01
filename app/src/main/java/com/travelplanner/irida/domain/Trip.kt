package com.travelplanner.irida.domain

data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val nights: Int,
    val budget: Double,
    val budgetSpent: Double,
    val emoji: String,
    val activities: List<ItineraryItem>
) {
    /**
     * Calcula el presupuesto restante después de las actividades planificadas.
     */
    fun getRemainingBudget(): Double {
        return budget - budgetSpent
    }

    /**
     * Calcula el porcentaje de presupuesto gastado.
     */
    fun getBudgetProgressPercent(): Int {
        return ((budgetSpent / budget) * 100).toInt()
    }

    /**
     * Future feature: optimización de distribución de presupuesto por día.
     */
    fun optimizeBudgetDistribution() {
        // @TODO Implementar algoritmo de distribución inteligente de presupuesto
    }
}