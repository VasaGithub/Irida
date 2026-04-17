package com.travelplanner.irida.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.travelplanner.irida.data.local.dao.ActivityDao
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.entity.toDomain
import com.travelplanner.irida.data.local.entity.toEntity
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val activityDao: ActivityDao,
    private val auth: FirebaseAuth
) : TripRepository {

    override fun getTripsStream(userId: String): Flow<List<Trip>> =
        tripDao.getTripsStream(userId).map { list -> list.map { it.toDomain() } }

    override fun getActivitiesStream(tripId: String): Flow<List<Activity>> =
        activityDao.getActivitiesStream(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTripById(id: String): Trip? =
        tripDao.getTripById(id)?.toDomain()

    override suspend fun isTitleDuplicate(title: String, userId: String, excludeId: String): Boolean =
        tripDao.countByTitle(title, userId, excludeId) > 0

    override suspend fun addTrip(trip: Trip) {
        tripDao.insert(trip.toEntity(auth.currentUser?.uid ?: ""))
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.update(trip.toEntity(trip.userId))
    }

    override suspend fun deleteTrip(tripId: String) {
        tripDao.delete(tripId)
    }

    override suspend fun addActivity(activity: Activity) {
        activityDao.insert(activity.toEntity())
    }

    override suspend fun updateActivity(activity: Activity) {
        activityDao.update(activity.toEntity())
    }

    override suspend fun deleteActivity(activityId: String) {
        activityDao.delete(activityId)
    }
}
