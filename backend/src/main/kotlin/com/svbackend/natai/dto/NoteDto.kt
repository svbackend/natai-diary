package com.svbackend.natai.dto

import java.util.Date
import java.util.UUID

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date? = null,
)

data class NewNoteDraft(
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
)

data class NewNote(
    val id: UUID,
    val userId: String,
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
)

data class UpdateNoteDraft(
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
    val updatedAt: Date,
)

data class UpdateNote(
    val id: UUID,
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
    val updatedAt: Date,
    val userId: String,
)