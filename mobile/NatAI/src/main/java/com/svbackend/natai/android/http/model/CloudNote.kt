package com.svbackend.natai.android.http.model

import java.util.*

data class CloudNote(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val deletedAt: Date? = null,
)