package com.svbackend.natai.android.http.dto

import com.svbackend.natai.android.entity.TagSet
import java.time.Instant
import java.time.LocalDate

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val actualDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val tags: TagSet = emptySet()
)
