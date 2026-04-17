package com.travelplanner.irida.data.repository

import com.travelplanner.irida.data.local.dao.UserDao
import com.travelplanner.irida.data.local.entity.UserEntity
import com.travelplanner.irida.domain.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserById(uid: String): UserEntity? =
        userDao.getUserById(uid)

    override suspend fun saveUser(user: UserEntity) =
        userDao.insert(user)

    override suspend fun isUsernameAvailable(username: String, excludeUid: String): Boolean =
        userDao.countByUsername(username, excludeUid) == 0
}
