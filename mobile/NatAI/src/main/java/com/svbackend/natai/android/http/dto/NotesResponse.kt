package com.svbackend.natai.android.http.dto

import java.time.Instant

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)
