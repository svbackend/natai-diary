package com.svbackend.natai.android.http.dto

import java.time.Instant

/**
{
"id": 0,
"url": "string",
"title": "string",
"description": "string",
"image": "string"
}
 */
data class CloudSuggestionLinkDto(
    val id: Int,
    val url: String,
    val title: String,
    val description: String,
    val image: String?,
)