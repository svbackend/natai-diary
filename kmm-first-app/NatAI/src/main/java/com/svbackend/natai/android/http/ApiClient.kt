package com.svbackend.natai.android.http

import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.dto.NewNoteRequest
import com.svbackend.natai.android.http.exception.NewNoteErrorException
import com.svbackend.natai.android.http.model.CloudNote
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import com.svbackend.natai.android.http.dto.Note as NoteDto

const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

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
            jackson()
        }
    }

    suspend fun getNotes(): List<Note> {
        val notes: List<NoteDto> = client.get("notes").body()

        return notes.map {
            Note(
                id = it.id,
                title = it.title,
                content = it.content,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
    }

    suspend fun addNote(note: Note): CloudNote {
        val body = NewNoteRequest(
            title = note.title,
            content = note.content,
        )

        val response = client.post("notes") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.Created) {
            throw NewNoteErrorException(response)
        }

        return response.body<CloudNote>()
    }
}