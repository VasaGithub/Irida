package com.travelplanner.irida

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelplanner.irida.data.local.IridaDatabase
import com.travelplanner.irida.data.local.dao.UserDao
import com.travelplanner.irida.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: IridaDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IridaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.userDao()
    }

    @After
    fun teardown() { db.close() }

    private fun user(uid: String, username: String = "user_$uid") = UserEntity(
        uid = uid, email = "$uid@test.com", username = username,
        birthdate = "", address = "", country = "", phone = "", acceptEmails = false
    )

    @Test
    fun insertAndGetById() = runTest {
        dao.insert(user("u1"))
        val result = dao.getUserById("u1")
        assertNotNull(result)
        assertEquals("u1", result!!.uid)
    }

    @Test
    fun getByIdReturnsNullIfNotFound() = runTest {
        assertNull(dao.getUserById("nonexistent"))
    }

    @Test
    fun countByUsernameDetectsDuplicate() = runTest {
        dao.insert(user("u1", username = "iker"))
        assertEquals(1, dao.countByUsername("iker", excludeUid = ""))
    }

    @Test
    fun countByUsernameExcludesOwnUid() = runTest {
        dao.insert(user("u1", username = "iker"))
        assertEquals(0, dao.countByUsername("iker", excludeUid = "u1"))
    }

    @Test
    fun updateChangesData() = runTest {
        dao.insert(user("u1", username = "iker"))
        dao.update(user("u1", username = "iker_updated"))
        assertEquals("iker_updated", dao.getUserById("u1")!!.username)
    }
}
