package com.svbackend.natai.android.http.exception

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

val statusToMessageMap = mapOf(
    HttpStatusCode.BadRequest to "Bad request",
    HttpStatusCode.UnprocessableEntity to "Unprocessable entity",
    HttpStatusCode.Unauthorized to "Unauthorized",
    HttpStatusCode.Forbidden to "Forbidden",
    HttpStatusCode.NotFound to "Not found",
    HttpStatusCode.InternalServerError to "Internal server error",
    HttpStatusCode.BadGateway to "Bad gateway",
    HttpStatusCode.ServiceUnavailable to "Service unavailable",
    HttpStatusCode.GatewayTimeout to "Gateway timeout",
)

class GenericHttpErrorException(val response: HttpResponse) : Exception() {
    override val message: String
        get() = statusToMessageMap[response.status] ?: "Unknown error"
}