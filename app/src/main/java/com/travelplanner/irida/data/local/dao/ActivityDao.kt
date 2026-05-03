package com.travelplanner.irida.data.local.dao

import androidx.room.*
import com.travelplanner.irida.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE tripId = :tripId ORDER BY date ASC, time ASC")
    fun getActivitiesStream(tripId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity)

    @Update
    suspend fun update(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun delete(activityId: String)
}
