package com.svbackend.natai.android.http

import android.net.Uri
import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.dto.NewNoteRequest
import com.svbackend.natai.android.http.dto.UpdateNoteRequest
import com.svbackend.natai.android.http.exception.CloudIdMissingException
import com.svbackend.natai.android.http.exception.DownloadAttachmentErrorException
import com.svbackend.natai.android.http.exception.FeatureNotAvailableException
import com.svbackend.natai.android.http.exception.GeneralFeedbackErrorException
import com.svbackend.natai.android.http.exception.LoginErrorException
import com.svbackend.natai.android.http.exception.NewNoteErrorException
import com.svbackend.natai.android.http.exception.RegistrationErrorException
import com.svbackend.natai.android.http.exception.SuggestionLinksErrorException
import com.svbackend.natai.android.http.exception.UpdateNoteErrorException
import com.svbackend.natai.android.http.request.AttachmentSignedUrlRequest
import com.svbackend.natai.android.http.request.GeneralFeedbackRequest
import com.svbackend.natai.android.http.request.LoginRequest
import com.svbackend.natai.android.http.request.RegisterRequest
import com.svbackend.natai.android.http.request.SuggestionFeedbackRequest
import com.svbackend.natai.android.http.response.AttachmentSignedUrlResponse
import com.svbackend.natai.android.http.response.AttachmentsResponse
import com.svbackend.natai.android.http.response.BuyFeatureResponse
import com.svbackend.natai.android.http.response.ErrorResponse
import com.svbackend.natai.android.http.response.LoginSuccessResponse
import com.svbackend.natai.android.http.response.NewNoteResponse
import com.svbackend.natai.android.http.response.NotesResponse
import com.svbackend.natai.android.http.response.RegisterSuccessResponse
import com.svbackend.natai.android.http.response.StaticSuccessResponse
import com.svbackend.natai.android.http.response.SuggestionLinksResponse
import com.svbackend.natai.android.http.response.SuggestionsResponse
import com.svbackend.natai.android.http.response.UserSuccessResponse
import com.svbackend.natai.android.query.UserQueryException
import com.svbackend.natai.android.utils.UtcDateTimeFormatter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.jackson.jackson
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.time.Instant

//const val BASE_URL = BuildConfig.API_BASE_URL
//const val BASE_URL = "https://natai.app"
const val BASE_URL = "https://legally-ideal-macaw.ngrok-free.app"

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
            requestTimeoutMillis = 10000
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
            level = LogLevel.ALL
        }
    }

    private val s3Client = HttpClient(Android)

    suspend fun getNotesForSync(updatedSince: Instant): NotesResponse {
        val response = client.get("/api/v1/sync") {
            val dt = UtcDateTimeFormatter.backendDateTime.format(updatedSince)
            parameter("updatedSince", dt)
        }

        return response.body()
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
            contentType(ContentType.Application.Json)
        }

        if (response.status != HttpStatusCode.OK) {
            throw LoginErrorException()
        }

        return response.body()
    }

    suspend fun register(request: RegisterRequest): RegisterSuccessResponse {
        Log.d(TAG, request.toString())

        val response = client.post("/api/v1/registration") {
            setBody(request)
        }

        if (response.status != HttpStatusCode.Created) {
            Log.d(TAG, "RegisterResponse: ${response.bodyAsText()}")
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

    suspend fun sendGeneralFeedback(req: GeneralFeedbackRequest) {
        try {
            val response = client.post("/api/v1/feedback") {
                setBody(req)
            }

            if (!response.status.isSuccess()) {
                throw GeneralFeedbackErrorException()
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message ?: "Error while sending suggestion feedback")
            throw e
        }
    }

    suspend fun deleteAccount() {
        client.delete("/api/v1/me")
    }

    suspend fun getSuggestionLinks(suggestionId: String): SuggestionLinksResponse {
        val response = client.get("/api/v1/suggestion/$suggestionId/links")

        if (response.status == HttpStatusCode.UnprocessableEntity) {
            val body = response.body<ErrorResponse>()

            if (body.code == "feature_not_available") {
                throw FeatureNotAvailableException()
            }

            throw SuggestionLinksErrorException(response)
        } else if (response.status != HttpStatusCode.OK) {
            throw SuggestionLinksErrorException(response)
        }

        return response.body()
    }

    suspend fun buySuggestionLinks(): BuyFeatureResponse {
        val response = client.post("/api/v1/links/buy")

        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }

        throw SuggestionLinksErrorException(response)
    }
}