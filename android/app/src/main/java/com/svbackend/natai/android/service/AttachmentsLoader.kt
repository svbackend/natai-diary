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

class AttachmentsLoader(
    private val api: ApiClient,
    private val fm: FileManagerService,
    private val repository: DiaryRepository,
    private val connectivityManager: ConnectivityManager,
) {
    val TAG = "AttachmentsLoader"
    // res/raw/placeholder.png
    private val placeholderUri: Uri =
        Uri.parse("android.resource://com.svbackend.natai.android/raw/placeholder")

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {
        Log.v(TAG, "=== LOAD ATTACHMENTS STARTED ===")

        val localAttachments = note.attachments.mapNotNull { attachment ->
            val uri = attachment.uri ?: placeholderUri
            val previewUri = attachment.previewUri ?: placeholderUri

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

        val attachmentsWithoutUri = note.attachments
            .filter { it.uri == null || !fm.isFileExists(it.uri) }
            .mapNotNull { it.cloudAttachmentId }

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

                if (cloudAttachment != null) {
                    val tmpUri = api.downloadAttachment(
                        cacheDir = fm.cacheDir,
                        signedUrl = cloudAttachment.signedUrl,
                    )
                    val uris = fm.processNewAttachment(tmpUri, cloudAttachment.originalFilename)
                    downloadedUrisMap[it] = uris
                }
            }

            // update local attachments

            val updatedAttachments = repository.updateAttachmentsUris(note, downloadedUrisMap)

            return updatedAttachments.mapNotNull { attachment ->
                val uri = attachment.uri ?: placeholderUri
                val previewUri = attachment.previewUri ?: placeholderUri

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
            ExistingLocalAttachmentDto(
                cloudAttachmentId = it.cloudAttachmentId,
                filename = it.filename,
                uri = it.uri ?: it.previewUri ?: placeholderUri,
                previewUri = it.previewUri,
            )
        }
    }
}