package com.travelplanner.irida.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.travelplanner.irida.data.local.converters.DateConverters
import com.travelplanner.irida.data.local.dao.AccessLogDao
import com.travelplanner.irida.data.local.dao.ActivityDao
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.dao.TripImageDao
import com.travelplanner.irida.data.local.dao.UserDao
import com.travelplanner.irida.data.local.entity.AccessLogEntity
import com.travelplanner.irida.data.local.entity.ActivityEntity
import com.travelplanner.irida.data.local.entity.TripEntity
import com.travelplanner.irida.data.local.entity.TripImageEntity
import com.travelplanner.irida.data.local.entity.UserEntity

@Database(
    entities = [
        TripEntity::class,
        ActivityEntity::class,
        UserEntity::class,
        AccessLogEntity::class,
        TripImageEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class IridaDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun tripImageDao(): TripImageDao
}

/** Adds the trip_images table introduced in version 2. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `trip_images` (
                `id`       TEXT    NOT NULL,
                `tripId`   TEXT    NOT NULL,
                `filePath` TEXT    NOT NULL,
                `addedAt`  INTEGER NOT NULL,
                PRIMARY KEY (`id`),
                FOREIGN KEY (`tripId`) REFERENCES `trips`(`id`) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_trip_images_tripId` ON `trip_images` (`tripId`)"
        )
    }
}
