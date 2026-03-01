package com.travelplanner.irida.domain

data class ItineraryItem(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    val location: String,
    val cost: Double,
    val emoji: String,
    val isBooked: Boolean = false
) {
    /**
     * Devuelve el coste formateado con símbolo de euro.
     */
    fun getFormattedCost(): String {
        return if (cost == 0.0) "Gratis" else "€${cost.toInt()}"
    }

    /**
     * Future feature: integrar con APIs de reservas externas.
     */
    fun syncWithBookingProvider() {
        // @TODO Implementar sincronización con proveedor de reservas
    }
}