package com.svbackend.natai.android.http.model

data class CloudNote(
    val id: String,
    val userId: String,
    val title: String,
    val content: String
)