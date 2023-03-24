package com.svbackend.natai.android.http.request

data class GeneralFeedbackRequest(
    val content: String,
    val stars: Int?
)
