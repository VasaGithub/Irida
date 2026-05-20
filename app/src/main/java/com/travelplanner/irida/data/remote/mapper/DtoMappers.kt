package com.travelplanner.irida.data.remote.mapper

import com.travelplanner.irida.data.remote.dto.HotelDto
import com.travelplanner.irida.data.remote.dto.ReservationDto
import com.travelplanner.irida.data.remote.dto.RoomDto
import com.travelplanner.irida.domain.Hotel
import com.travelplanner.irida.domain.Reservation
import com.travelplanner.irida.domain.Room
import java.time.LocalDate

fun HotelDto.toDomain(): Hotel = Hotel(
    id = id,
    name = name,
    address = address,
    rating = rating,
    imageUrl = image_url,
    rooms = rooms?.map { it.toDomain() } ?: emptyList()
)

fun RoomDto.toDomain(): Room = Room(
    id = id,
    roomType = room_type,
    price = price,
    images = images ?: emptyList()
)

fun ReservationDto.toDomain(): Reservation = Reservation(
    id = id,
    hotelId = hotel_id,
    roomId = room_id,
    startDate = LocalDate.parse(start_date),
    endDate = LocalDate.parse(end_date),
    guestName = guest_name,
    guestEmail = guest_email,
    hotel = hotel?.toDomain(),
    room = room?.toDomain()
)
