package com.svbackend.natai.android.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

fun Instant.isAfterSecs(other: Instant): Boolean {
    return this.truncatedTo(ChronoUnit.SECONDS).isAfter(other.truncatedTo(ChronoUnit.SECONDS))
}