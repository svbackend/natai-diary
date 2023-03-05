package com.svbackend.natai.android.http.response

data class AttachmentSignedUrlResponse(
    val uploadUrl: String,
    val attachmentId: String,
    val expiresAt: String,
)
