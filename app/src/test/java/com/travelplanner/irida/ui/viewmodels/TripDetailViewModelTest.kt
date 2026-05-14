package com.travelplanner.irida.ui.viewmodels

import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var viewModel: TripDetailViewModel

    private val trip = Trip(
        id = "trip-1",
        title = "Tokio",
        description = "Viaje",
        startDate = LocalDate.of(2026, 5, 10),
        endDate = LocalDate.of(2026, 5, 20)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeTripRepository().apply { seedTrip(trip) }
        viewModel = TripDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addActivity con datos validos llama al repositorio y limpia errores`() = runTest {
        viewModel.loadTrip(trip.id)
        advanceUntilIdle()

        val ok = viewModel.addActivity(
            title = "Visita",
            description = "Entrada a las 10",
            date = LocalDate.of(2026, 5, 15),
            time = LocalTime.of(10, 0)
        )
        advanceUntilIdle()

        assertTrue("La validación debe pasar", ok)
        assertEquals(1, repository.addedActivities.size)
        assertEquals("Visita", repository.addedActivities.first().title)
        assertTrue(viewModel.validationErrors.value.isEmpty())
    }

    @Test
    fun `addActivity con titulo vacio no llama al repositorio y publica error`() = runTest {
        viewModel.loadTrip(trip.id)
        advanceUntilIdle()

        val ok = viewModel.addActivity(
            title = "",
            description = "desc",
            date = LocalDate.of(2026, 5, 15),
            time = LocalTime.of(10, 0)
        )
        advanceUntilIdle()

        assertFalse(ok)
        assertTrue(repository.addedActivities.isEmpty())
        assertNotNull(viewModel.validationErrors.value["title"])
    }

    @Test
    fun `addActivity con fecha fuera del rango del trip publica error en date`() = runTest {
        viewModel.loadTrip(trip.id)
        advanceUntilIdle()

        val ok = viewModel.addActivity(
            title = "Visita",
            description = "desc",
            date = LocalDate.of(2026, 6, 1),
            time = LocalTime.of(10, 0)
        )
        advanceUntilIdle()

        assertFalse(ok)
        assertTrue(repository.addedActivities.isEmpty())
        assertNotNull(viewModel.validationErrors.value["date"])
    }

    @Test
    fun `clearValidationErrors vacia el mapa`() = runTest {
        viewModel.loadTrip(trip.id)
        advanceUntilIdle()
        viewModel.addActivity("", "", null, null)
        advanceUntilIdle()
        assertFalse(viewModel.validationErrors.value.isEmpty())

        viewModel.clearValidationErrors()

        assertTrue(viewModel.validationErrors.value.isEmpty())
    }

    @Test
    fun `addActivity sin trip cargado devuelve false`() {
        val ok = viewModel.addActivity(
            "Visita", "desc",
            LocalDate.of(2026, 5, 15), LocalTime.of(10, 0)
        )
        assertFalse(ok)
        assertTrue(repository.addedActivities.isEmpty())
    }

    private class FakeTripRepository : TripRepository {
        private val trips = mutableMapOf<String, Trip>()
        private val activitiesByTrip = mutableMapOf<String, MutableStateFlow<List<Activity>>>()
        val addedActivities = mutableListOf<Activity>()

        fun seedTrip(trip: Trip) {
            trips[trip.id] = trip
            activitiesByTrip.getOrPut(trip.id) { MutableStateFlow(emptyList()) }
        }

        override fun getTripsStream(userId: String): Flow<List<Trip>> =
            MutableStateFlow(trips.values.toList()).asStateFlow()

        override fun getActivitiesStream(tripId: String): Flow<List<Activity>> =
            activitiesByTrip.getOrPut(tripId) { MutableStateFlow(emptyList()) }.asStateFlow()

        override suspend fun getTripById(id: String): Trip? = trips[id]

        override suspend fun isTitleDuplicate(title: String, userId: String, excludeId: String): Boolean = false

        override suspend fun addTrip(trip: Trip) { trips[trip.id] = trip }

        override suspend fun updateTrip(trip: Trip) { trips[trip.id] = trip }

        override suspend fun deleteTrip(tripId: String) { trips.remove(tripId) }

        override suspend fun addActivity(activity: Activity) {
            addedActivities.add(activity)
            val flow = activitiesByTrip.getOrPut(activity.tripId) { MutableStateFlow(emptyList()) }
            flow.value = flow.value + activity
        }

        override suspend fun updateActivity(activity: Activity) {
            val flow = activitiesByTrip[activity.tripId] ?: return
            flow.value = flow.value.map { if (it.id == activity.id) activity else it }
        }

        override suspend fun deleteActivity(activityId: String) {
            activitiesByTrip.values.forEach { flow ->
                flow.value = flow.value.filterNot { it.id == activityId }
            }
        }
    }
}
