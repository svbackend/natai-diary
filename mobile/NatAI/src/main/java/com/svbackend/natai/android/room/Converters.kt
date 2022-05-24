package com.svbackend.natai.android.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from).toInstant()
        }
    }

    @TypeConverter
    fun fromInstant(date: Instant?): String? {
        return date?.let {
            return formatter.format(OffsetDateTime.ofInstant(date, ZoneOffset.UTC))
        }
    }
}