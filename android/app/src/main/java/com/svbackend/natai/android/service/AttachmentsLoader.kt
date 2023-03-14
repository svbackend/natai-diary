package com.svbackend.natai.android.service

import android.net.ConnectivityManager
import android.net.Uri
import com.svbackend.natai.android.entity.ExistingAttachmentDto
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
    // res/raw/placeholder.png
    private val placeholderUri: Uri =
        Uri.parse("android.resource://com.svbackend.natai.android/raw/placeholder")

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {
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

        if (!hasInternetConnection(connectivityManager)) {
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
}