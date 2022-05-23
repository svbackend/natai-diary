package com.svbackend.natai.android.http.dto

import java.util.*

data class UpdateNoteRequest(
    val title: String,
    val content: String,
    val updatedAt: Date,
    val deletedAt: Date? = null
)
