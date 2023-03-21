package com.svbackend.natai.android.http

import android.net.Uri
import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.dto.CloudSuggestionDto
import com.svbackend.natai.android.http.dto.NewNoteRequest
import com.svbackend.natai.android.http.dto.UpdateNoteRequest
import com.svbackend.natai.android.http.exception.*
import com.svbackend.natai.android.http.request.AttachmentSignedUrlRequest
import com.svbackend.natai.android.http.request.LoginRequest
import com.svbackend.natai.android.http.request.RegisterRequest
import com.svbackend.natai.android.http.request.SuggestionFeedbackRequest
import com.svbackend.natai.android.http.response.*
import com.svbackend.natai.android.query.UserQueryException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

//const val BASE_URL = BuildConfig.API_BASE_URL
const val BASE_URL = "https://natai.app"
//const val BASE_URL = "https://f226-24-203-8-51.ngrok.io"

class ApiClient(
    private val getApiToken: () -> String?
) {
    val TAG = "ApiClient"

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
            requestTimeoutMillis = 5000
        }
    }

    private val s3Client = HttpClient(Android)

    suspend fun getNotesForSync(): NotesResponse {
        return client.get("/api/v1/notes").body()
    }

    suspend fun addNote(note: LocalNote): NewNoteResponse {
        val body = NewNoteRequest(
            title = note.title,
            content = note.content,
            deletedAt = note.deletedAt,
            actualDate = note.actualDate,
            tags = note.tags,
            attachments = note.attachments.mapNotNull { it.cloudAttachmentId },
        )

        val response = client.post("/api/v2/notes") {
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
            attachments = note.attachments.mapNotNull { it.cloudAttachmentId },
        )

        val response = client.put("/api/v2/notes/${note.cloudId}") {
            setBody(body)
        }

        if (response.status != HttpStatusCode.OK) {
            throw UpdateNoteErrorException(response)
        }
    }

    suspend fun login(request: LoginRequest): LoginSuccessResponse {
        val response = client.post("/api/v1/login") {
            setBody(request)
        }

        if (response.status != HttpStatusCode.OK) {
            throw LoginErrorException()
        }

        return response.body()
    }

    suspend fun register(request: RegisterRequest): RegisterSuccessResponse {
        val response = client.post("/api/v1/registration") {
            setBody(request)
        }

        if (response.status != HttpStatusCode.Created) {
            throw RegistrationErrorException()
        }

        return response.body()
    }

    suspend fun getCurrentUser(): UserSuccessResponse {
        val response = client.get("/api/v1/me")

        if (response.status != HttpStatusCode.OK) {
            throw UserQueryException("You need to be authenticated to get user information")
        }

        return response.body()
    }

    suspend fun getStatic(): StaticSuccessResponse {
        val response = client.get("/api/v1/static")

        if (response.status != HttpStatusCode.OK) {
            return StaticSuccessResponse(
                terms = "Terms of service are not available at the moment"
            )
        }

        return response.body()
    }

    @OptIn(InternalAPI::class)
    suspend fun uploadFile(
        inputStream: InputStream,
        contentType: String,
        uploadUrl: String,
        onProgress: (Double) -> Unit,
        onFinish: () -> Unit,
    ) {
        s3Client.put(uploadUrl) {
            header("Content-Type", contentType)
            body = inputStream.readBytes()

            onUpload { bytesSentTotal, contentLength ->
                onProgress(bytesSentTotal / contentLength.toDouble())
            }
        }

        onFinish()
    }

    @OptIn(InternalAPI::class)
    suspend fun uploadFileSync(
        inputStream: InputStream,
        contentType: String,
        uploadUrl: String,
    ) = withContext(Dispatchers.IO) {
        s3Client.put(uploadUrl) {
            header("Content-Type", contentType)
            body = inputStream.readBytes()
        }
    }

    suspend fun getAttachmentSignedUrl(
        originalFilename: String
    ): AttachmentSignedUrlResponse {
        val response = client.post("/api/v1/attachments") {
            setBody(
                AttachmentSignedUrlRequest(
                    filename = originalFilename,
                )
            )
        }

        return response.body()
    }

    suspend fun getAttachmentsByNote(noteId: String): AttachmentsResponse {
        val response = client.get("/api/v1/notes/${noteId}/attachments")

        if (response.status != HttpStatusCode.OK) {
            Log.v(TAG, response.body())
            throw DownloadAttachmentErrorException()
        }

        return response.body()
    }

    // get file from s3, convert to temporary file and return its uri
    suspend fun downloadAttachment(cacheDir: File, signedUrl: String): Uri {
        val fileResponse = s3Client.get(signedUrl)

        if (fileResponse.status != HttpStatusCode.OK) {
            Log.v(TAG, fileResponse.bodyAsText())
            throw DownloadAttachmentErrorException()
        }

        val file = fileResponse.body<ByteArray>()
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("attachment", null, cacheDir)
        }
        tempFile.writeBytes(file)

        return Uri.fromFile(tempFile)
    }

    suspend fun getSuggestions(): SuggestionsResponse {
        try {
            val response = client.get("/api/v1/suggestions")
            return response.body()
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e(TAG, e.message ?: "Error while getting suggestions")
            return SuggestionsResponse(emptyList())
        }
    }

    suspend fun sendSuggestionFeedback(suggestionId: String, req: SuggestionFeedbackRequest) {
        try {
            client.put("/api/v1/suggestions/$suggestionId/feedback") {
                setBody(req)
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message ?: "Error while sending suggestion feedback")
        }
    }
}