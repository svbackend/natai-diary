package com.svbackend.natai.dto

data class NoteDto(
    val id: Int,
    val title: String,
    val content: String,
)

data class NewNoteDraft(
    val title: String,
    val content: String
)

data class NewNote(
    val userId: String,
    val title: String,
    val content: String
)
