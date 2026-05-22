package com.travelplanner.irida.domain

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for trip photo management.
 * All I/O (file copy + DB insert) happens inside the implementations.
 */
interface TripImageRepository {

    /** Reactive stream of images for the given trip, ordered oldest-first. */
    fun getImagesForTrip(tripId: String): Flow<List<TripImage>>

    /** Persists [image] to the database (the caller is responsible for the file copy). */
    suspend fun addImage(image: TripImage)

    /** Deletes the DB row AND the file on disk for [imageId]. */
    suspend fun deleteImage(imageId: String)
}
