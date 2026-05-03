package com.travelplanner.irida.data.local.dao

import androidx.room.*
import com.travelplanner.irida.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE username = :username AND uid != :excludeUid")
    suspend fun countByUsername(username: String, excludeUid: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)
}
