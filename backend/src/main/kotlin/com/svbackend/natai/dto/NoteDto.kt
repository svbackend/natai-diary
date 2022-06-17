package com.svbackend.natai.dto

import java.time.*
import java.util.*

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)

data class NewNoteDraft(
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
)

data class NewNote(
    val id: UUID,
    val userId: String,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
)

data class UpdateNoteDraft(
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val updatedAt: Instant,
)

data class UpdateNote(
    val id: UUID,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val deletedAt: Instant? = null,
    val updatedAt: Instant,
    val userId: String,
)