package com.travelplanner.irida

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelplanner.irida.data.local.IridaDatabase
import com.travelplanner.irida.data.local.dao.ActivityDao
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.entity.ActivityEntity
import com.travelplanner.irida.data.local.entity.TripEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ActivityDaoTest {

    private lateinit var db: IridaDatabase
    private lateinit var activityDao: ActivityDao
    private lateinit var tripDao: TripDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            IridaDatabase::class.java
        ).allowMainThreadQueries().build()
        activityDao = db.activityDao()
        tripDao = db.tripDao()
    }

    @After
    fun teardown() { db.close() }

    private fun sampleTrip(id: String) = TripEntity(
        id = id, userId = "user1", title = "Trip", description = "desc",
        startDate = LocalDate.of(2025, 1, 1), endDate = LocalDate.of(2025, 1, 7),
        destination = "Spain", nights = 6, budget = 0.0, budgetSpent = 0.0, emoji = "✈️"
    )

    private fun activity(id: String, tripId: String) = ActivityEntity(
        id = id, tripId = tripId, title = "Act $id", description = "desc",
        date = LocalDate.of(2025, 1, 2), time = LocalTime.of(10, 0)
    )

    @Test
    fun insertAndGetByStream() = runTest {
        tripDao.insert(sampleTrip("t1"))
        activityDao.insert(activity("a1", "t1"))
        val result = activityDao.getActivitiesStream("t1").first()
        assertEquals(1, result.size)
        assertEquals("a1", result[0].id)
    }

    @Test
    fun deleteRemovesActivity() = runTest {
        tripDao.insert(sampleTrip("t1"))
        activityDao.insert(activity("a1", "t1"))
        activityDao.delete("a1")
        val result = activityDao.getActivitiesStream("t1").first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun cascadeDeleteRemovesActivitiesWhenTripDeleted() = runTest {
        tripDao.insert(sampleTrip("t1"))
        activityDao.insert(activity("a1", "t1"))
        activityDao.insert(activity("a2", "t1"))
        tripDao.delete("t1")
        val result = activityDao.getActivitiesStream("t1").first()
        assertTrue(result.isEmpty())
    }
}
