package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.model.CloudNote

data class NotesResponse(
    val notes: List<CloudNote>,
)

