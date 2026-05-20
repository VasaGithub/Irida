package com.travelplanner.irida.domain

data class Room(
    val id: String,
    val roomType: String,
    val price: Double,
    val images: List<String> = emptyList()
)
