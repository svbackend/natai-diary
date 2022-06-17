package com.svbackend.natai.android.http

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.svbackend.natai.android.BuildConfig
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.dto.NewNoteRequest
import com.svbackend.natai.android.http.dto.UpdateNoteRequest
import com.svbackend.natai.android.http.exception.CloudIdMissingException
import com.svbackend.natai.android.http.exception.NewNoteErrorException
import com.svbackend.natai.android.http.exception.UpdateNoteErrorException
import com.svbackend.natai.android.http.model.CloudNote
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import com.svbackend.natai.android.http.dto.NoteDto as NoteDto

const val BASE_URL = BuildConfig.API_BASE_URL + "/api/v1/"
//const val BASE_URL = "https://natai.app/api/v1/"

class ApiClient(
    private val getApiToken: () -> String?
) {
    private val client = HttpClient(Android) {
        defaultRequest {
            val apiKey = getApiToken()
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            url(BASE_URL)
        }
        install(ContentNegotiation) {
            jackson {
                registerModule(JavaTimeModule())
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 4350
        }

    }

    suspend fun getNotes(): List<Note> {
        val notes: List<NoteDto> = client.get("notes").body()

        return notes.map {
            Note(
                id = it.id,
                title = it.title,
                content = it.content,
                actualDate = it.actualDate,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                deletedAt = it.deletedAt,
            )
        }
    }

    suspend fun getNotesForSync(): List<Note> {
        val notes: List<NoteDto> = client.get("notes/sync").body()

        return notes.map {
            Note(
                id = it.id,
                title = it.title,
                content = it.content,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                deletedAt = it.deletedAt,
            )
        }
    }

    suspend fun addNote(note: Note): CloudNote {
        val body = NewNoteRequest(
            title = note.title,
            content = note.content,
            deletedAt = note.deletedAt,
            actualDate = note.actualDate,
        )

        val response = client.post("notes") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.Created) {
            throw NewNoteErrorException(response)
        }

        return response.body<CloudNote>()
    }

    suspend fun updateNote(note: Note) {
        if (note.cloudId == null) {
            throw CloudIdMissingException()
        }

        val body = UpdateNoteRequest(
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            deletedAt = note.deletedAt,
            actualDate = note.actualDate,
        )

        val response = client.put("notes/${note.cloudId}") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.OK) {
            throw UpdateNoteErrorException(response)
        }
    }
}