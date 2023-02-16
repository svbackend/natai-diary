package com.svbackend.natai.android.http.model

data class CloudTag(
    val id: Int,
    val tag: String,
    val score: Int? = null,
)