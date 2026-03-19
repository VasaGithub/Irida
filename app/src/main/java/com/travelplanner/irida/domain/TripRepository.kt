package com.travelplanner.irida.domain

interface TripRepository {

    // ── TRIPS ──────────────────────────────────────────────────────────────

    /**
     * Devuelve la lista actual de todos los viajes.
     */
    fun getTrips(): List<Trip>

    /**
     *
     * Busca un viaje por su [id]. Devuelve null si no existe.
     */
    fun getTripById(id: String): Trip?

    /**
     * Añade un nuevo [trip] al repositorio.
     */
    fun addTrip(trip: Trip)

    /**
     * Reemplaza el viaje con el mismo [trip].id por los nuevos datos.
     * No hace nada si el viaje no existe.
     */
    fun updateTrip(trip: Trip)

    /**
     * Elimina el viaje con el [tripId] indicado y todas sus actividades.
     */
    fun deleteTrip(tripId: String)

    // ── ACTIVITIES ─────────────────────────────────────────────────────────

    /**
     * Devuelve las actividades del viaje con [tripId].
     */
    fun getActivities(tripId: String): List<Activity>

    /**
     * Añade una nueva [activity] al viaje correspondiente.
     */
    fun addActivity(activity: Activity)

    /**
     * Reemplaza la actividad con el mismo [activity].id por los nuevos datos.
     */
    fun updateActivity(activity: Activity)

    /**
     * Elimina la actividad con el [activityId] indicado.
     */
    fun deleteActivity(activityId: String)
}