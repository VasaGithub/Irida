package com.travelplanner.irida.data.local.entity

import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.TripImage

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    destination = destination,
    nights = nights,
    budget = budget,
    budgetSpent = budgetSpent,
    emoji = emoji
)

fun Trip.toEntity(userId: String): TripEntity = TripEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    destination = destination,
    nights = nights,
    budget = budget,
    budgetSpent = budgetSpent,
    emoji = emoji
)

fun ActivityEntity.toDomain(): Activity = Activity(
    id = id,
    tripId = tripId,
    title = title,
    description = description,
    date = date,
    time = time
)

fun Activity.toEntity(): ActivityEntity = ActivityEntity(
    id = id,
    tripId = tripId,
    title = title,
    description = description,
    date = date,
    time = time
)

// ── TripImage ────────────────────────────────────────────────────────────────

fun TripImageEntity.toDomain(): TripImage = TripImage(
    id       = id,
    tripId   = tripId,
    filePath = filePath,
    addedAt  = addedAt
)

fun TripImage.toEntity(): TripImageEntity = TripImageEntity(
    id       = id,
    tripId   = tripId,
    filePath = filePath,
    addedAt  = addedAt
)
