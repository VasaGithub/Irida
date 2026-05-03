package com.travelplanner.irida.domain

import com.travelplanner.irida.data.local.entity.UserEntity

interface UserRepository {
    suspend fun getUserById(uid: String): UserEntity?
    suspend fun saveUser(user: UserEntity)
    suspend fun isUsernameAvailable(username: String, excludeUid: String = ""): Boolean
}