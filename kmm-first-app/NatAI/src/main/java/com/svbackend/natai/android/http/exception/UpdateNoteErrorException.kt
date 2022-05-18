package com.svbackend.natai.android.http.exception

import io.ktor.client.statement.*

class UpdateNoteErrorException(val response: HttpResponse) : Exception() {
    override val message: String
        get() = "Error during cloud update, status = ${response.status}"
}