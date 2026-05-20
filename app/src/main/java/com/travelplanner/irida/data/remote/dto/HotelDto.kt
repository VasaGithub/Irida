package com.travelplanner.irida.data.remote.dto

data class HotelDto(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val image_url: String,
    val rooms: List<RoomDto>? = null
)
