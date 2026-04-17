package com.travelplanner.irida.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val username: String,
    val birthdate: String,
    val address: String,
    val country: String,
    val phone: String,
    val acceptEmails: Boolean
)
