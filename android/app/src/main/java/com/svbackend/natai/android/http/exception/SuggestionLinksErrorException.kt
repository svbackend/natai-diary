package com.svbackend.natai.android.http.exception

import io.ktor.client.statement.*
import io.ktor.http.*

class SuggestionLinksErrorException(val response: HttpResponse) : Exception() {
    override val message: String
        get() = "Error when loading suggestion links, status: ${response.status}"
}