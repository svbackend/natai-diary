package com.svbackend.natai.android.http.dto

import java.time.Instant

data class CloudSuggestionDto(
    val id: String,
    val notes: List<String>,
    val suggestion: String,
    val period: SuggestionPeriodDto,
    val isReceived: Boolean,
    val feedbackRating: Int?,
    val createdAt: Instant,
    val suggestionLinksCount: Int
)