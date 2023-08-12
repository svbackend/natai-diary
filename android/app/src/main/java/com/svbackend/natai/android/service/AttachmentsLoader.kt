package com.svbackend.natai.android.service

import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.ExistingLocalAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.utils.hasInternetConnection
import com.svbackend.natai.android.utils.filePreviewPlaceholderUri

class AttachmentsLoader(
    private val api: ApiClient,
    private val fm: FileManagerService,
    private val repository: DiaryRepository,
    private val connectivityManager: ConnectivityManager,
) {
    val TAG = "AttachmentsLoader"

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {
        Log.v(TAG, "=== LOAD ATTACHMENTS STARTED ===")

        val localAttachments = note.attachments.mapNotNull { attachment ->
            val previewUri = attachment.previewUri ?: filePreviewPlaceholderUri
            val uri = attachment.uri ?: previewUri

            if (attachment.cloudAttachmentId == null) {
                return@mapNotNull null
            }

            ExistingAttachmentDto.create(
                attachment.cloudAttachmentId,
                attachment.filename,
                uri,
                previewUri
            )
        }

        Log.v(TAG, "=== LOCAL ATTACHMENTS ===")
        Log.v(TAG, localAttachments.toString())

        if (!hasInternetConnection(connectivityManager)) {
            Log.v(TAG, "=== NO INTERNET, RETURNING ===")
            return localAttachments
        }

        val isUriExists = { uri: Uri? ->
            uri != null && fm.isFileExists(uri)
        }

        val attachmentsWithoutUri = note.attachments
            .filter { !isUriExists(it.uri) || (it.previewUri != null && !isUriExists(it.previewUri)) }
            .mapNotNull { it.cloudAttachmentId }

        Log.v(TAG, "=== ATTACHMENTS WITHOUT URI ===")
        Log.v(TAG, attachmentsWithoutUri.toString())

        if (note.cloudId == null || attachmentsWithoutUri.isEmpty()) {
            // nothing to load
            return localAttachments
        }

        try {
            val cloudAttachments = api
                .getAttachmentsByNote(note.cloudId)
                .attachments

            val downloadedUrisMap =
                mutableMapOf<String, AttachmentUris>() // cloudAttachmentId to uris

            attachmentsWithoutUri.forEach {
                val cloudAttachment = cloudAttachments.find { cloudAttachment ->
                    cloudAttachment.attachmentId == it
                }

                val localAttachment = note.attachments.find { localAttachment ->
                    localAttachment.cloudAttachmentId == it
                }

                if (cloudAttachment != null && localAttachment != null) {
                    val originalFileUri = if (localAttachment.uri != null && isUriExists(localAttachment.uri)) {
                        Log.v(TAG, "=== USING LOCAL ATTACHMENT URI ===")
                        localAttachment.uri
                    } else api.downloadAttachment(
                        cacheDir = fm.cacheDir,
                        signedUrl = cloudAttachment.signedUrl,
                    )
                    Log.v(TAG, "=== URI = $originalFileUri ===")

                    try {
                        val uris = fm.processExistingAttachment(originalFileUri)
                        downloadedUrisMap[it] = uris
                    } catch (e: Throwable) {
                        Log.v(TAG, "=== ERROR PROCESSING ATTACHMENT (fm.processNewAttachment) ===")
                        e.printStackTrace()
                    }
                }
            }

            // update local attachments

            val updatedAttachments = repository.updateAttachmentsUris(note, downloadedUrisMap)

            return updatedAttachments.mapNotNull { attachment ->
                val uri = attachment.uri ?: filePreviewPlaceholderUri
                val previewUri = attachment.previewUri ?: filePreviewPlaceholderUri

                if (attachment.cloudAttachmentId == null) {
                    return@mapNotNull null
                }

                ExistingAttachmentDto.create(
                    attachment.cloudAttachmentId,
                    attachment.filename,
                    uri,
                    previewUri
                )
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            return localAttachments
        }
    }

    fun loadLocalAttachments(note: LocalNote): List<ExistingLocalAttachmentDto> {
        return note.attachments.map {
            val uri: Uri? = it.uri
            var previewUri: Uri? = it.previewUri

            val needToGeneratePreview = previewUri == null && uri != null && fm.isFileExists(uri)

            if (needToGeneratePreview) {
                try {
                    val uris = fm.processExistingAttachment(uri!!)
                    previewUri = uris.previewUri
                } catch (e: Throwable) {
                    Log.v(TAG, "=== ERROR processExistingAttachment ===")
                    e.printStackTrace()
                }
            }

            ExistingLocalAttachmentDto(
                cloudAttachmentId = it.cloudAttachmentId,
                filename = it.filename,
                uri = it.uri ?: previewUri ?: filePreviewPlaceholderUri,
                previewUri = previewUri,
            )
        }
    }
}