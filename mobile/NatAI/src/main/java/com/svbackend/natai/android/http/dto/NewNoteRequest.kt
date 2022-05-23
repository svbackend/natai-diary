package com.svbackend.natai.android.http.dto

import java.util.*

data class NewNoteRequest(
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
)
