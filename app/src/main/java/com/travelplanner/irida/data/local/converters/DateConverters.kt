package com.travelplanner.irida.data.local.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class DateConverters {
    @TypeConverter fun fromLocalDate(d: LocalDate?): String? = d?.toString()
    @TypeConverter fun toLocalDate(s: String?): LocalDate? = s?.let { LocalDate.parse(it) }
    @TypeConverter fun fromLocalTime(t: LocalTime?): String? = t?.toString()
    @TypeConverter fun toLocalTime(s: String?): LocalTime? = s?.let { LocalTime.parse(it) }
}