package com.svbackend.natai.android.http.model

data class CloudTag(
    val id: Int,
    val name: String,
    val score: Int? = null,
)