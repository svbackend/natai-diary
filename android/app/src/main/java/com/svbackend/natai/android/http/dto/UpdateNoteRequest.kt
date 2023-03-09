package com.svbackend.natai.android.http.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.svbackend.natai.android.entity.TagEntityDto
import java.time.Instant
import java.time.LocalDate

data class UpdateNoteRequest(
    val title: String,
    val content: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    val updatedAt: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    val deletedAt: Instant? = null,
    val actualDate: LocalDate,
    val tags: List<TagEntityDto> = emptyList(),
    val attachments: List<String> = emptyList(),
)
