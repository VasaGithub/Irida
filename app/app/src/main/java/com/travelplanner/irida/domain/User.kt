package com.travelplanner.irida.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val preferences: Preferences
) {
    fun updateProfile() {
        // @TODO Implement network call to sync profile changes with the server
    }
}