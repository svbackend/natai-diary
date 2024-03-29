package com.svbackend.natai.android.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LocalDateTimeFormatter {
    val fullDateTime: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd.MM.y HH:mm")
        .withZone(ZoneId.systemDefault())

    val fullDate: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd.MM.y")
        .withZone(ZoneId.systemDefault())

    val ymd: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())

    val day: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd")
        .withZone(ZoneId.systemDefault())

    val monthShortName: DateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM")
        .withZone(ZoneId.systemDefault())

    val year: DateTimeFormatter = DateTimeFormatter
        .ofPattern("y")
        .withZone(ZoneId.systemDefault())

    val time: DateTimeFormatter = DateTimeFormatter
        .ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    val fullDateAmerica = DateTimeFormatter
        .ofPattern("MMM d yyyy")
        .withZone(ZoneId.systemDefault())
}

object UtcDateTimeFormatter {
    val backendDateTime: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.of("UTC"))
}