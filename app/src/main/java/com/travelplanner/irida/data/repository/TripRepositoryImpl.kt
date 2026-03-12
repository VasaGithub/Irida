package com.travelplanner.irida.data.repository

import android.util.Log
import com.travelplanner.irida.data.fakeDB.FakeTripDataSource
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository

class TripRepositoryImpl private constructor(
    private val dataSource: FakeTripDataSource = FakeTripDataSource
) : TripRepository {

    companion object {
        val instance: TripRepositoryImpl by lazy { TripRepositoryImpl() }
    }

    private val TAG = "TripRepositoryImpl"

    // ── TRIPS ──────────────────────────────────────────────────────────────

    override fun getTrips(): List<Trip> {
        Log.d(TAG, "getTrips llamado")
        return dataSource.getTrips()
    }

    override fun getTripById(id: String): Trip? {
        Log.d(TAG, "getTripById llamado con id=$id")
        return dataSource.getTripById(id)
    }

    override fun addTrip(trip: Trip) {
        Log.d(TAG, "addTrip llamado: '${trip.title}'")
        dataSource.addTrip(trip)
    }

    override fun updateTrip(trip: Trip) {
        Log.d(TAG, "updateTrip llamado: '${trip.title}' (id=${trip.id})")
        dataSource.updateTrip(trip)
    }

    override fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip llamado: id=$tripId")
        dataSource.deleteTrip(tripId)
    }

    // ── ACTIVITIES ─────────────────────────────────────────────────────────

    override fun getActivities(tripId: String): List<Activity> {
        Log.d(TAG, "getActivities llamado: tripId=$tripId")
        return dataSource.getActivities(tripId)
    }

    override fun addActivity(activity: Activity) {
        Log.d(TAG, "addActivity llamado: '${activity.title}' (tripId=${activity.tripId})")
        dataSource.addActivity(activity)
    }

    override fun updateActivity(activity: Activity) {
        Log.d(TAG, "updateActivity llamado: '${activity.title}' (id=${activity.id})")
        dataSource.updateActivity(activity)
    }

    override fun deleteActivity(activityId: String) {
        Log.d(TAG, "deleteActivity llamado: id=$activityId")
        dataSource.deleteActivity(activityId)
    }
}