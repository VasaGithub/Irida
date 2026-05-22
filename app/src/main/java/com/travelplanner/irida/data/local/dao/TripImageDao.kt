package com.travelplanner.irida.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.travelplanner.irida.data.local.entity.TripImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripImageDao {
    @Query("SELECT * FROM trip_images WHERE tripId = :tripId ORDER BY addedAt ASC")
    fun getImagesForTrip(tripId: String): Flow<List<TripImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: TripImageEntity)

    @Query("DELETE FROM trip_images WHERE id = :imageId")
    suspend fun deleteById(imageId: String)

    @Query("SELECT filePath FROM trip_images WHERE id = :imageId LIMIT 1")
    suspend fun getFilePathById(imageId: String): String?
}
