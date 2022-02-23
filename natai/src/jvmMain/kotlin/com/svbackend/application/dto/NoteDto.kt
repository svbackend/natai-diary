package com.svbackend.application.dto

data class NoteDto(
    val id: Int = 1,
    val title: String = "Hello World",
    val content: String = "Note content",
)
