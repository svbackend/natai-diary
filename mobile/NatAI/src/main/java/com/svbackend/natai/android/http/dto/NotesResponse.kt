package com.svbackend.natai.android.http.dto

import java.util.*

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
)
