package com.travelplanner.irida.domain

/**
 * Domain model for a photo attached to a trip.
 *
 * @param id        Random UUID assigned when the image is imported.
 * @param tripId    FK to the parent trip.
 * @param filePath  Absolute path inside [android.content.Context.getFilesDir] where the copy is
 *                  stored. Never a content:// URI — we always copy the bytes on import so that the
 *                  image remains accessible even after the source is deleted from the gallery.
 * @param addedAt   Epoch millis from [System.currentTimeMillis] at insertion time.
 */
data class TripImage(
    val id: String,
    val tripId: String,
    val filePath: String,
    val addedAt: Long
)
