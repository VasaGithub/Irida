package com.travelplanner.irida.data.remote.dto

data class RoomDto(
    val id: String,
    val room_type: String,
    val price: Double,
    val images: List<String>? = null
)
