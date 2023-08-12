package com.svbackend.natai.android.http.exception

import io.ktor.client.statement.*
import io.ktor.http.*

class BuyFeatureErrorException(val response: HttpResponse) : Exception() {
    override val message: String
        get() = "Error when initializing stripe customer, checkout session or payment intent, status=${response.status}"
}