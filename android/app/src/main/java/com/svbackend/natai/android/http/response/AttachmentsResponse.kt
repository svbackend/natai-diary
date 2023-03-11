package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.model.CloudNote

data class AttachmentsResponse(
    val notes: List<CloudNote>,
)

