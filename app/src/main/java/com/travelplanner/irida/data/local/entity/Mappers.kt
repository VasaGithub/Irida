package com.travelplanner.irida.data.local.entity

import com.travelplanner.irida.domain.Activity
import com.travelplanner.irida.domain.Trip
import com.travelplanner.irida.domain.User

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

fun UserEntity.toDomain(): User = User(
    uid = uid,
    email = email,
    username = username,
    birthdate = birthdate,
    address = address,
    country = country,
    phone = phone,
    acceptEmails = acceptEmails
)

fun User.toEntity(): UserEntity = UserEntity(
    uid = uid,
    email = email,
    username = username,
    birthdate = birthdate,
    address = address,
    country = country,
    phone = phone,
    acceptEmails = acceptEmails
)
