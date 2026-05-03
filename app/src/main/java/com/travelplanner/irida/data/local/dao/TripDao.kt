package com.travelplanner.irida.data.local.dao

import androidx.room.*
import com.travelplanner.irida.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startDate ASC")
    fun getTripsStream(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: String): TripEntity?

    @Query("SELECT COUNT(*) FROM trips WHERE title = :title AND userId = :userId AND id != :excludeId")
    suspend fun countByTitle(title: String, userId: String, excludeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity)

    @Update
    suspend fun update(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun delete(tripId: String)
}
