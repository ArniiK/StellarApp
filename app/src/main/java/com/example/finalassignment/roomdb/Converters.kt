package com.example.finalassignment.roomdb

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class Converters {
    @TypeConverter
    fun toDate(dateString: String?): Instant? {
        return if (dateString == null) {
            null
        } else {
            Instant.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: Instant?): String? {
        return if (date == null) {
            null
        } else {
            date.toString()
        }
    }
}