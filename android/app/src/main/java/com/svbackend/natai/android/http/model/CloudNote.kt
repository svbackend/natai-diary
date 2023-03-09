package com.svbackend.natai.android.http.model

import java.time.Instant
import java.time.LocalDate

data class CloudNote(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val actualDate: LocalDate = LocalDate.now(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null,
    val tags: List<CloudTag> = emptyList(),
    val attachments: List<CloudAttachment> = emptyList(),
)