package com.travelplanner.irida.domain
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTripsStream(userId: String): Flow<List<Trip>>
    fun getActivitiesStream(tripId: String): Flow<List<Activity>>
    suspend fun getTripById(id: String): Trip?
    suspend fun isTitleDuplicate(title: String, userId: String, excludeId: String = ""): Boolean
    suspend fun addTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(tripId: String)
    suspend fun addActivity(activity: Activity)
    suspend fun updateActivity(activity: Activity)
    suspend fun deleteActivity(activityId: String)
}
