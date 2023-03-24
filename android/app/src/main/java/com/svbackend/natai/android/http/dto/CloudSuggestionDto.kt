package com.svbackend.natai.android.http.dto

import java.time.Instant

/**
public UuidV4 $id,
public array $notes,
public string $suggestion,
public SuggestionPeriodDto $period,
public bool $isReceived,
public ?int $feedbackRating,
public \DateTimeInterface $createdAt,
 */
data class CloudSuggestionDto(
    val id: String,
    val notes: List<String>,
    val suggestion: String,
    val period: SuggestionPeriodDto,
    val isReceived: Boolean,
    val feedbackRating: Int?,
    val createdAt: Instant,
)