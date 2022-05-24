package com.svbackend.natai.android.http.dto

import java.time.Instant

data class NewNoteRequest(
    val title: String,
    val content: String,
    val deletedAt: Instant? = null,
)
