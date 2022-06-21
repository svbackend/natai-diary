package com.svbackend.natai.dto

import java.time.*
import java.util.*

data class NewNoteDraft(
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val tags: List<TagDto>,
)

data class NewNote(
    val id: UUID,
    val userId: String,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val tags: List<TagDto>,
)

data class UpdateNoteDraft(
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val updatedAt: Instant,
    val tags: List<TagDto>,
)

data class UpdateNote(
    val id: UUID,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val updatedAt: Instant,
    val userId: String,
    val tags: List<TagDto>,
)