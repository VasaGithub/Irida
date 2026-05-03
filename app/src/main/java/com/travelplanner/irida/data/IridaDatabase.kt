package com.travelplanner.irida.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.travelplanner.irida.data.local.converters.DateConverters
import com.travelplanner.irida.data.local.dao.AccessLogDao
import com.travelplanner.irida.data.local.dao.ActivityDao
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.dao.UserDao
import com.travelplanner.irida.data.local.entity.AccessLogEntity
import com.travelplanner.irida.data.local.entity.ActivityEntity
import com.travelplanner.irida.data.local.entity.TripEntity
import com.travelplanner.irida.data.local.entity.UserEntity

@Database(
    entities = [TripEntity::class, ActivityEntity::class, UserEntity::class, AccessLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class IridaDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
}
