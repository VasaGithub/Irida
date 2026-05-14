package com.travelplanner.irida.domain

data class User(
    val uid: String,
    val email: String,
    val username: String,
    val birthdate: String,
    val address: String,
    val country: String,
    val phone: String,
    val acceptEmails: Boolean
) {
    /** Iniciales del usuario a partir de [username]. */
    fun getInitials(): String {
        // @TODO: devolver hasta 2 iniciales en mayúsculas, manejando nombres vacíos o con varias palabras.
        return username.take(1).uppercase()
    }

    /** Edad calculada a partir de [birthdate]. */
    fun getAgeYears(): Int? {
        // @TODO: parsear birthdate (definir formato canónico, p.ej. ISO yyyy-MM-dd)
        // y calcular años completos hasta la fecha actual. Devolver null si no es parseable.
        return null
    }
}
