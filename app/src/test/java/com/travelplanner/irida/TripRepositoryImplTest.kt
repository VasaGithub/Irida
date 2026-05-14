package com.travelplanner.irida.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.travelplanner.irida.data.local.dao.ActivityDao
import com.travelplanner.irida.data.local.dao.TripDao
import com.travelplanner.irida.data.local.entity.ActivityEntity
import com.travelplanner.irida.data.local.entity.TripEntity
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TripRepositoryImplTest {

    private lateinit var tripDao: TripDao
    private lateinit var activityDao: ActivityDao
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() {
        tripDao = mockk(relaxed = true)
        activityDao = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        every { auth.currentUser } returns null
        repository = TripRepositoryImpl(tripDao, activityDao, auth)
    }

    private fun sampleTrip(id: String = "trip-${UUID.randomUUID()}") = Trip(
        id = id,
        title = "Viaje de Prueba",
        description = "Descripción de prueba",
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(3),
        userId = "user-1"
    )

    private fun sampleActivity(tripId: String, id: String = "act-${UUID.randomUUID()}") = Activity(
        id = id,
        tripId = tripId,
        title = "Visita al Museo",
        description = "Entrada a las 10",
        date = LocalDate.now(),
        time = LocalTime.of(10, 0)
    )

    @Test
    fun `addTrip delega en TripDao insert con la entidad correcta`() = runTest {
        val trip = sampleTrip()
        coEvery { tripDao.insert(any()) } just Runs

        repository.addTrip(trip)

        coVerify(exactly = 1) { tripDao.insert(match<TripEntity> { it.id == trip.id && it.title == trip.title }) }
    }

    @Test
    fun `getTripById devuelve el dominio mapeado desde el DAO`() = runTest {
        val tripId = "trip-1"
        val entity = TripEntity(
            id = tripId, userId = "user-1", title = "Tokio", description = "Desc",
            startDate = LocalDate.now(), endDate = LocalDate.now().plusDays(2),
            destination = "Tokio", nights = 2, budget = 0.0, budgetSpent = 0.0, emoji = "✈️"
        )
        coEvery { tripDao.getTripById(tripId) } returns entity

        val result = repository.getTripById(tripId)

        assertNotNull(result)
        assertEquals("Tokio", result?.title)
    }

    @Test
    fun `getTripById devuelve null si el DAO no encuentra el viaje`() = runTest {
        coEvery { tripDao.getTripById("missing") } returns null

        val result = repository.getTripById("missing")

        assertNull(result)
    }

    @Test
    fun `updateTrip delega en TripDao update con la entidad mapeada`() = runTest {
        val trip = sampleTrip().copy(title = "Viaje Modificado")
        coEvery { tripDao.update(any()) } just Runs

        repository.updateTrip(trip)

        coVerify(exactly = 1) { tripDao.update(match<TripEntity> { it.id == trip.id && it.title == "Viaje Modificado" }) }
    }

    @Test
    fun `deleteTrip delega en TripDao delete con el id`() = runTest {
        coEvery { tripDao.delete(any()) } just Runs

        repository.deleteTrip("trip-x")

        coVerify(exactly = 1) { tripDao.delete("trip-x") }
    }

    @Test
    fun `isTitleDuplicate devuelve true cuando el conteo es mayor que cero`() = runTest {
        coEvery { tripDao.countByTitle("Tokio", "user-1", "") } returns 1

        val duplicated = repository.isTitleDuplicate("Tokio", "user-1")

        assertTrue(duplicated)
    }

    @Test
    fun `isTitleDuplicate devuelve false cuando el conteo es cero`() = runTest {
        coEvery { tripDao.countByTitle("Tokio", "user-1", "") } returns 0

        val duplicated = repository.isTitleDuplicate("Tokio", "user-1")

        assertFalse(duplicated)
    }

    @Test
    fun `addActivity delega en ActivityDao insert con la entidad correcta`() = runTest {
        val activity = sampleActivity("trip-1")
        coEvery { activityDao.insert(any()) } just Runs

        repository.addActivity(activity)

        coVerify(exactly = 1) {
            activityDao.insert(match<ActivityEntity> { it.id == activity.id && it.tripId == "trip-1" })
        }
    }

    @Test
    fun `deleteActivity delega en ActivityDao delete con el id`() = runTest {
        coEvery { activityDao.delete(any()) } just Runs

        repository.deleteActivity("act-x")

        coVerify(exactly = 1) { activityDao.delete("act-x") }
    }
}
