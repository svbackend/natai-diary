package com.svbackend.natai.android.http

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.svbackend.natai.android.BuildConfig
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.dto.NewNoteRequest
import com.svbackend.natai.android.http.dto.UpdateNoteRequest
import com.svbackend.natai.android.http.exception.*
import com.svbackend.natai.android.http.model.CloudNote
import com.svbackend.natai.android.http.request.LoginRequest
import com.svbackend.natai.android.http.request.RegisterRequest
import com.svbackend.natai.android.http.response.*
import com.svbackend.natai.android.query.UserQueryException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

const val BASE_URL = BuildConfig.API_BASE_URL + "/api/v1/"
//const val BASE_URL = "https://natai.app/api/v1/"
//const val BASE_URL = "https://b966-24-203-8-51.ngrok.io/api/v1/"

class ApiClient(
    private val getApiToken: () -> String?
) {
    private val client = HttpClient(Android) {
        defaultRequest {
            val apiKey = getApiToken()
            header("X-API-TOKEN", apiKey)
            contentType(ContentType.Application.Json)
            url(BASE_URL)
        }
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                registerModule(JavaTimeModule())
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 4350
        }

    }

    suspend fun getNotesForSync(): NotesResponse {
        return client.get("notes").body()
    }

    suspend fun addNote(note: LocalNote): NewNoteResponse {
        val body = NewNoteRequest(
            title = note.title,
            content = note.content,
            deletedAt = note.deletedAt,
            actualDate = note.actualDate,
            tags = note.tags,
        )

        val response = client.post("notes") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.Created) {
            throw NewNoteErrorException(response)
        }

        return response.body<NewNoteResponse>() // backend responds with {noteId: uuid} of created note
    }

    suspend fun updateNote(note: LocalNote) {
        if (note.cloudId == null) {
            throw CloudIdMissingException()
        }

        val body = UpdateNoteRequest(
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            deletedAt = note.deletedAt,
            actualDate = note.actualDate,
            tags = note.tags,
        )

        val response = client.put("notes/${note.cloudId}") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.OK) {
            throw UpdateNoteErrorException(response)
        }
    }

    suspend fun login(request: LoginRequest): LoginSuccessResponse {
        val response = client.post("login") {
            setBody(request)
        }

        if (response.status != HttpStatusCode.OK) {
            throw LoginErrorException()
        }

        return response.body()
    }

    suspend fun register(request: RegisterRequest): RegisterSuccessResponse {
        val response = client.post("registration") {
            setBody(request)
        }

        if (response.status != HttpStatusCode.Created) {
            throw RegistrationErrorException()
        }

        return response.body()
    }

    suspend fun getCurrentUser(): UserSuccessResponse {
        val response = client.get("me")

        if (response.status != HttpStatusCode.OK) {
            throw UserQueryException("You need to be authenticated to get user information")
        }

        return response.body()
    }

    suspend fun getStatic(): StaticSuccessResponse {
        val response = client.get("static")

        if (response.status != HttpStatusCode.OK) {
            return StaticSuccessResponse(
                terms = "Terms of service are not available at the moment"
            )
        }

        return response.body()
    }


}