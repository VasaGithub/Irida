package com.travelplanner.irida.ui.viewmodels

import com.google.firebase.auth.FirebaseAuth
import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripRepository
import io.mockk.every
import io.mockk.mockk
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TripListViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: TripListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeTripRepository()
        auth = mockk(relaxed = true)
        every { auth.currentUser } returns null
        viewModel = TripListViewModel(repository, auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTrip con datos validos llama al repositorio y limpia errores`() = runTest {
        val ok = viewModel.addTrip(
            title = "Tokio",
            description = "Viaje a Japón",
            destination = "Tokio",
            startDate = LocalDate.of(2026, 5, 10),
            endDate = LocalDate.of(2026, 5, 20)
        )
        advanceUntilIdle()

        assertTrue("La validación debe pasar", ok)
        assertEquals(1, repository.addedTrips.size)
        assertEquals("Tokio", repository.addedTrips.first().title)
        assertTrue(viewModel.validationErrors.value.isEmpty())
    }

    @Test
    fun `addTrip con titulo vacio no llama al repositorio y publica error`() = runTest {
        val ok = viewModel.addTrip(
            title = "",
            description = "desc",
            destination = "Tokio",
            startDate = LocalDate.of(2026, 5, 10),
            endDate = LocalDate.of(2026, 5, 20)
        )
        advanceUntilIdle()

        assertFalse(ok)
        assertTrue(repository.addedTrips.isEmpty())
        assertNotNull(viewModel.validationErrors.value["title"])
    }

    @Test
    fun `addTrip con endDate anterior a startDate publica error en endDate`() = runTest {
        val ok = viewModel.addTrip(
            title = "Tokio",
            description = "desc",
            destination = "Tokio",
            startDate = LocalDate.of(2026, 5, 20),
            endDate = LocalDate.of(2026, 5, 10)
        )
        advanceUntilIdle()

        assertFalse(ok)
        assertTrue(repository.addedTrips.isEmpty())
        assertNotNull(viewModel.validationErrors.value["endDate"])
    }

    @Test
    fun `addTrip con fechas nulas publica errores para ambas fechas`() = runTest {
        val ok = viewModel.addTrip(
            title = "Tokio",
            description = "desc",
            destination = "Tokio",
            startDate = null,
            endDate = null
        )
        advanceUntilIdle()

        assertFalse(ok)
        assertNotNull(viewModel.validationErrors.value["startDate"])
        assertNotNull(viewModel.validationErrors.value["endDate"])
    }

    @Test
    fun `clearValidationErrors vacia el mapa`() = runTest {
        viewModel.addTrip("", "", "", null, null)
        advanceUntilIdle()
        assertFalse(viewModel.validationErrors.value.isEmpty())

        viewModel.clearValidationErrors()

        assertTrue(viewModel.validationErrors.value.isEmpty())
    }

    private class FakeTripRepository : TripRepository {
        private val trips = mutableMapOf<String, Trip>()
        val addedTrips = mutableListOf<Trip>()

        override fun getTripsStream(userId: String): Flow<List<Trip>> =
            MutableStateFlow(trips.values.toList()).asStateFlow()

        override fun getActivitiesStream(tripId: String): Flow<List<Activity>> =
            MutableStateFlow(emptyList<Activity>()).asStateFlow()

        override suspend fun getTripById(id: String): Trip? = trips[id]

        override suspend fun isTitleDuplicate(title: String, userId: String, excludeId: String): Boolean = false

        override suspend fun addTrip(trip: Trip) {
            addedTrips.add(trip)
            trips[trip.id] = trip
        }

        override suspend fun updateTrip(trip: Trip) { trips[trip.id] = trip }

        override suspend fun deleteTrip(tripId: String) { trips.remove(tripId) }

        override suspend fun addActivity(activity: Activity) {}
        override suspend fun updateActivity(activity: Activity) {}
        override suspend fun deleteActivity(activityId: String) {}
    }
}
