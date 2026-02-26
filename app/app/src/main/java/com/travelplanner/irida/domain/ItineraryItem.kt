package com.travelplanner.irida.domain

data class ItineraryItem(
    val id: String,
    val tripId: String,
    val title: String,
    val description: String,
    val cost: Double,
    val date: String
) {
    fun calculateDuration() {
        // @TODO Implement logic to calculate time between start and end of the activity
    }
}