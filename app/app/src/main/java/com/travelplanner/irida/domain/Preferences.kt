package com.travelplanner.irida.domain

data class Preferences(
    val userId: String,
    var notificationsEnabled: Boolean,
    var preferredLanguage: String,
    var theme: String
) {
    fun updatePreferences() {
        // @TODO Implement local storage saving for user preferences
    }
}