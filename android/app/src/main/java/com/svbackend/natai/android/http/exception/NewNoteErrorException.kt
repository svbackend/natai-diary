package com.svbackend.natai.android.http.exception

import io.ktor.client.statement.*
import io.ktor.http.*

class NewNoteErrorException(val response: HttpResponse) : Exception() {
    override val message: String
        get() = "New note error! Expected ${HttpStatusCode.Created} but ${response.status} given"
}