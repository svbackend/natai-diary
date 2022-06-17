package com.svbackend.natai.android.room

import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class Converters {
    /* Instant */
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

    @TypeConverter
    fun isoDateToLocalDate(value: String?): LocalDate? {
        return value?.let {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
        }
    }

    @TypeConverter
    fun localDateToIsoDate(date: LocalDate?): String? {
        return date?.let {
            return DateTimeFormatter.ISO_DATE.format(it)
        }
    }
}