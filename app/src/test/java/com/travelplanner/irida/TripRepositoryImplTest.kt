package com.travelplanner.irida.data.repository

import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TripRepositoryImplTest {

    // Usamos la instancia Singleton de tu repositorio
    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() {
        repository = TripRepositoryImpl.instance
    }

    // ── TESTS DE VIAJES (TRIPS) ────────────────────────────────────────────

    @Test
    fun `addTrip inserta un viaje correctamente en el repositorio`() {
        // 1. Arrange (Preparación)
        val testTripId = "trip-add-${UUID.randomUUID()}"
        val newTrip = Trip(
            id = testTripId,
            title = "Viaje de Prueba",
            description = "Descripción de prueba",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(3)
        )

        // 2. Act (Acción)
        repository.addTrip(newTrip)
        val savedTrip = repository.getTripById(testTripId)

        // 3. Assert (Comprobación)
        assertNotNull("El viaje no debería ser nulo tras guardarse", savedTrip)
        assertEquals("El título debe coincidir", "Viaje de Prueba", savedTrip?.title)
    }

    @Test
    fun `updateTrip modifica los datos de un viaje existente`() {
        // 1. Arrange
        val testTripId = "trip-update-${UUID.randomUUID()}"
        val trip = Trip(testTripId, "Viaje Original", "Desc", LocalDate.now(), LocalDate.now().plusDays(1))
        repository.addTrip(trip)

        // 2. Act
        val modifiedTrip = trip.copy(title = "Viaje Modificado", description = "Nueva desc")
        repository.updateTrip(modifiedTrip)

        val updatedTrip = repository.getTripById(testTripId)

        // 3. Assert
        assertNotNull(updatedTrip)
        assertEquals("El título debería haberse actualizado", "Viaje Modificado", updatedTrip?.title)
        assertEquals("La descripción debería haberse actualizado", "Nueva desc", updatedTrip?.description)
    }

    @Test
    fun `deleteTrip elimina el viaje del repositorio`() {
        // 1. Arrange
        val testTripId = "trip-delete-${UUID.randomUUID()}"
        val trip = Trip(testTripId, "Viaje a Borrar", "Desc", LocalDate.now(), LocalDate.now())
        repository.addTrip(trip)

        // 2. Act
        repository.deleteTrip(testTripId)
        val deletedTrip = repository.getTripById(testTripId)

        // 3. Assert
        assertNull("El viaje debería ser nulo tras haber sido eliminado", deletedTrip)
    }

    // ── TESTS DE ACTIVIDADES (ACTIVITIES) ──────────────────────────────────

    @Test
    fun `addActivity asigna correctamente una actividad a un viaje`() {
        // 1. Arrange
        val tripId = "trip-act-${UUID.randomUUID()}"
        val activityId = "act-${UUID.randomUUID()}"
        repository.addTrip(Trip(tripId, "Viaje", "Desc", LocalDate.now(), LocalDate.now()))

        val newActivity = Activity(
            id = activityId,
            tripId = tripId,
            title = "Visita al Museo",
            description = "Entrada a las 10",
            date = LocalDate.now(),
            time = LocalTime.of(10, 0)
        )

        // 2. Act
        repository.addActivity(newActivity)
        val activities = repository.getActivities(tripId)

        // 3. Assert
        assertTrue("La lista de actividades debería contener la nueva actividad", activities.any { it.id == activityId })
        assertEquals("El título de la actividad debe coincidir", "Visita al Museo", activities.find { it.id == activityId }?.title)
    }

    @Test
    fun `deleteActivity elimina la actividad de la lista del viaje`() {
        // 1. Arrange
        val tripId = "trip-del-act-${UUID.randomUUID()}"
        val activityId = "act-del-${UUID.randomUUID()}"
        repository.addTrip(Trip(tripId, "Viaje", "Desc", LocalDate.now(), LocalDate.now()))

        val activity = Activity(activityId, tripId, "Cena", "", LocalDate.now(), LocalTime.of(20, 0))
        repository.addActivity(activity)

        // 2. Act
        repository.deleteActivity(activityId)
        val activitiesAfterDelete = repository.getActivities(tripId)

        // 3. Assert
        assertFalse("La actividad eliminada ya no debería estar en la lista", activitiesAfterDelete.any { it.id == activityId })
    }
}