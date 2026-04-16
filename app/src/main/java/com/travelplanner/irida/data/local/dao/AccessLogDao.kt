package com.travelplanner.irida.data.local.dao

import androidx.room.*
import com.travelplanner.irida.data.local.entity.AccessLogEntity

@Dao
interface AccessLogDao {
    @Insert
    suspend fun insert(log: AccessLogEntity)

    @Query("SELECT * FROM access_logs WHERE userId = :userId ORDER BY datetime DESC")
    suspend fun getLogsForUser(userId: String): List<AccessLogEntity>
}
