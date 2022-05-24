package com.svbackend.natai.android.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter

val localDateTimeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.y HH:mm")
    .withZone(ZoneId.systemDefault())