package com.travelplanner.irida.data.fakeDB

import android.util.Log
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import java.time.LocalDate
import java.time.LocalTime

object FakeTripDataSource {

    private const val TAG = "FakeTripDataSource"

    // ── Almacenamiento en memoria ──────────────────────────────────────────

    private val trips = mutableListOf<Trip>()
    private val activities = mutableListOf<Activity>()

    // ── Inicialización con fake dataset ───────────────────────────────────

    init {
        seedFakeData()
        Log.d(TAG, "FakeTripDataSource inicializado con ${trips.size} viajes y ${activities.size} actividades")
    }

    private fun seedFakeData() {
        val today = LocalDate.now()

        val trip1 = Trip(
            id = "trip-001",
            title = "Aventura en Tokio",
            description = "Explorando la cultura japonesa: templos, gastronomía y tecnología.",
            startDate = today.plusDays(10),
            endDate = today.plusDays(18)
        )
        val trip2 = Trip(
            id = "trip-002",
            title = "Escapada a París",
            description = "Ciudad de la luz: museos, gastronomía y arquitectura.",
            startDate = today.plusDays(30),
            endDate = today.plusDays(34)
        )
        val trip3 = Trip(
            id = "trip-003",
            title = "Evasión en Bali",
            description = "Playas paradisíacas, templos y retiros de bienestar.",
            startDate = today.plusDays(60),
            endDate = today.plusDays(70)
        )

        trips.addAll(listOf(trip1, trip2, trip3))

        activities.addAll(
            listOf(
                Activity(
                    id = "act-001",
                    tripId = "trip-001",
                    title = "Visita Templo Senso-ji",
                    description = "Templo budista en Asakusa, el más antiguo de Tokio.",
                    date = today.plusDays(11),
                    time = LocalTime.of(9, 0)
                ),
                Activity(
                    id = "act-002",
                    tripId = "trip-001",
                    title = "Cruce de Shibuya",
                    description = "El cruce peatonal más famoso del mundo.",
                    date = today.plusDays(12),
                    time = LocalTime.of(15, 30)
                ),
                Activity(
                    id = "act-003",
                    tripId = "trip-002",
                    title = "Museo del Louvre",
                    description = "Visita a las colecciones permanentes, incluida la Mona Lisa.",
                    date = today.plusDays(31),
                    time = LocalTime.of(10, 0)
                )
            )
        )
    }

    // ── TRIPS ──────────────────────────────────────────────────────────────

    fun getTrips(): List<Trip> {
        Log.d(TAG, "getTrips: devolviendo ${trips.size} viajes")
        return trips.toList()
    }

    fun getTripById(id: String): Trip? {
        val trip = trips.find { it.id == id }
        if (trip == null) {
            Log.e(TAG, "getTripById: viaje con id=$id no encontrado")
        }
        return trip
    }

    fun addTrip(trip: Trip) {
        trips.add(trip)
        Log.i(TAG, "addTrip: viaje '${trip.title}' añadido (id=${trip.id})")
    }

    fun updateTrip(trip: Trip) {
        val index = trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            trips[index] = trip
            Log.i(TAG, "updateTrip: viaje '${trip.title}' actualizado (id=${trip.id})")
        } else {
            Log.e(TAG, "updateTrip: viaje con id=${trip.id} no encontrado, no se actualizó")
        }
    }

    fun deleteTrip(tripId: String) {
        val removed = trips.removeAll { it.id == tripId }
        val removedActivities = activities.removeAll { it.tripId == tripId }
        if (removed) {
            Log.i(TAG, "deleteTrip: viaje id=$tripId eliminado junto con sus actividades (removedActivities=$removedActivities)")
        } else {
            Log.e(TAG, "deleteTrip: viaje con id=$tripId no encontrado")
        }
    }

    // ── ACTIVITIES ─────────────────────────────────────────────────────────

    fun getActivities(tripId: String): List<Activity> {
        val result = activities.filter { it.tripId == tripId }
        Log.d(TAG, "getActivities: ${result.size} actividades para tripId=$tripId")
        return result
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
        Log.i(TAG, "addActivity: '${activity.title}' añadida (id=${activity.id}, tripId=${activity.tripId})")
    }

    fun updateActivity(activity: Activity) {
        val index = activities.indexOfFirst { it.id == activity.id }
        if (index != -1) {
            activities[index] = activity
            Log.i(TAG, "updateActivity: '${activity.title}' actualizada (id=${activity.id})")
        } else {
            Log.e(TAG, "updateActivity: actividad con id=${activity.id} no encontrada")
        }
    }

    fun deleteActivity(activityId: String) {
        val removed = activities.removeAll { it.id == activityId }
        if (removed) {
            Log.i(TAG, "deleteActivity: actividad id=$activityId eliminada")
        } else {
            Log.e(TAG, "deleteActivity: actividad con id=$activityId no encontrada")
        }
    }
}