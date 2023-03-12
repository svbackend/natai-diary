package com.svbackend.natai.android.service

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.svbackend.natai.android.DiaryApplication
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.ApiClient
import java.io.File

class FileManagerService(
    private val apiClient: ApiClient,
    private val contentResolver: ContentResolver,
    private val filesDir: File,
    private val cacheDir: File,
) {
    fun copyFileToInternalStorage(uri: Uri, originalFilename: String): Uri {
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IllegalArgumentException("$uri - not found")
        }

        val file = File(filesDir, originalFilename)
        val internalUri = file.toUri()

        inputStream.use { input ->
            contentResolver.openOutputStream(internalUri, "w").use { output ->
                if (output == null) {
                    throw IllegalArgumentException("$internalUri - not created")
                }

                input.copyTo(output)
            }
        }

        return internalUri
    }

    fun deleteFile(uri: Uri) {
        try {
            uri.toFile().delete()
        } catch (e: Exception) {
            println("------- DELETE FILE ERROR $uri")
            e.printStackTrace()
        }
    }

    fun isFileExists(uri: Uri): Boolean {
        return try {
            uri.toFile().exists()
        } catch (e: Exception) {
            println("------- FILE EXISTS ERROR $uri")
            e.printStackTrace()
            false
        }
    }

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {
        println("loadAttachments")

        // res/raw/placeholder.png
        val placeholderUri =
            Uri.parse("android.resource://com.svbackend.natai.android/raw/placeholder")

        val localAttachments = note.attachments.map { attachment ->
            val uri = attachment.uri ?: placeholderUri
            ExistingAttachmentDto.create(attachment, uri)
        }

        val attachmentsIdsWithoutUri = note.attachments
            .filter { it.uri == null || !isFileExists(it.uri) }
            .mapNotNull { it.cloudAttachmentId }

        if (note.cloudId == null || attachmentsIdsWithoutUri.isEmpty()) {
            // nothing to load
            return localAttachments
        }

        try {
            val cloudAttachments = apiClient
                .getAttachmentsByNote(note.cloudId, attachmentsIdsWithoutUri)
                .attachments

            println(cloudAttachments)

            val downloadedUrisMap = mutableMapOf<String, Uri>() // cloudAttachmentId to uri

            attachmentsIdsWithoutUri.forEach {
                val cloudAttachment = cloudAttachments.find { cloudAttachment ->
                    cloudAttachment.attachmentId == it
                }

                if (cloudAttachment != null) {
                    val uri = apiClient.downloadAttachment(cacheDir, cloudAttachment.signedUrl)
                    downloadedUrisMap[it] = uri
                }
            }

            println(downloadedUrisMap)

            return localAttachments.map { localAttachment ->
                val uri = downloadedUrisMap[localAttachment.cloudAttachmentId]
                if (uri != null) {
                    localAttachment.copy(uri = uri)
                } else localAttachment
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            return localAttachments
        }
    }
}