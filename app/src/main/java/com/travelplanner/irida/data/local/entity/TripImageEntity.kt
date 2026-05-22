package com.travelplanner.irida.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity that stores the path of a photo associated with a trip.
 *
 * A foreign-key to [TripEntity] ensures rows are removed automatically when the
 * parent trip is deleted (CASCADE).
 */
@Entity(
    tableName = "trip_images",
    foreignKeys = [
        ForeignKey(
            entity        = TripEntity::class,
            parentColumns = ["id"],
            childColumns  = ["tripId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class TripImageEntity(
    @PrimaryKey val id: String,
    val tripId: String,

    /** Absolute path inside filesDir. */
    val filePath: String,

    /** Insertion timestamp (epoch millis). */
    val addedAt: Long
)
