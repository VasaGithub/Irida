package com.travelplanner.irida.domain

data class Trip(
    val id: String,
    val title: String,
    val budget: Double,
    val activities: List<ItineraryItem>
) {
    fun getRemainingBudget(): Double {
        val totalActivityCost = activities.sumOf { it.cost }
        return budget - totalActivityCost
    }

    fun optimizeBudgetDistribution() {
        // @TODO Implement smart budget distribution algorithm
    }
}