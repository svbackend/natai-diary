package com.svbackend.natai.android.http.dto

import java.time.Instant

data class SuggestionPeriodDto(
    val from: Instant,
    val to: Instant,
)
