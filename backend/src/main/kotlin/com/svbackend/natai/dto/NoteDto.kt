package com.svbackend.natai.dto

import java.util.UUID

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
)

data class NewNoteDraft(
    val title: String,
    val content: String
)

data class NewNote(
    val id: UUID,
    val userId: String,
    val title: String,
    val content: String
)
