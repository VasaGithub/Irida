package com.travelplanner.irida

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelplanner.irida.data.local.IridaDatabase
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.entity.TripEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var db: IridaDatabase
    private lateinit var dao: TripDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IridaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.tripDao()
    }

    @After
    fun teardown() { db.close() }

    private fun trip(id: String, userId: String, title: String = "Trip $id") = TripEntity(
        id = id, userId = userId, title = title, description = "desc",
        startDate = LocalDate.of(2025, 1, 1), endDate = LocalDate.of(2025, 1, 7),
        destination = "Spain", nights = 6, budget = 1000.0, budgetSpent = 0.0, emoji = "✈️"
    )

    @Test
    fun insertAndGetById() = runTest {
        dao.insert(trip("1", "user1"))
        val result = dao.getTripById("1")
        assertNotNull(result)
        assertEquals("1", result!!.id)
    }

    @Test
    fun getTripsStreamFiltersByUserId() = runTest {
        dao.insert(trip("1", "user1"))
        dao.insert(trip("2", "user2"))
        dao.insert(trip("3", "user1"))
        val trips = dao.getTripsStream("user1").first()
        assertEquals(2, trips.size)
        assertTrue(trips.all { it.userId == "user1" })
    }

    @Test
    fun deleteRemovesTrip() = runTest {
        dao.insert(trip("1", "user1"))
        dao.delete("1")
        assertNull(dao.getTripById("1"))
    }

    @Test
    fun countByTitleDetectsDuplicate() = runTest {
        dao.insert(trip("1", "user1", title = "Viaje a Roma"))
        val count = dao.countByTitle("Viaje a Roma", "user1", excludeId = "")
        assertEquals(1, count)
    }

    @Test
    fun countByTitleExcludesOwnId() = runTest {
        dao.insert(trip("1", "user1", title = "Viaje a Roma"))
        val count = dao.countByTitle("Viaje a Roma", "user1", excludeId = "1")
        assertEquals(0, count)
    }
}
