package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.model.CloudAttachment

data class AttachmentsResponse(
    val attachments: List<CloudAttachment>,
)

