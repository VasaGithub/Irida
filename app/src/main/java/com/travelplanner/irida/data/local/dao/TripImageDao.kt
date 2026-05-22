package com.travelplanner.irida.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.travelplanner.irida.data.local.entity.TripImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripImageDao {

    /** Reactive list of images for a trip, sorted oldest-first. */
    @Query("SELECT * FROM trip_images WHERE tripId = :tripId ORDER BY addedAt ASC")
    fun getImagesForTrip(tripId: String): Flow<List<TripImageEntity>>

    /** Inserts or replaces (idempotent on re-import). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: TripImageEntity)

    /** Removes a single image row by its primary key. */
    @Query("DELETE FROM trip_images WHERE id = :imageId")
    suspend fun deleteById(imageId: String)

    /** Returns the filePath for a given imageId (used by the repo to delete the file). */
    @Query("SELECT filePath FROM trip_images WHERE id = :imageId LIMIT 1")
    suspend fun getFilePathById(imageId: String): String?
}
