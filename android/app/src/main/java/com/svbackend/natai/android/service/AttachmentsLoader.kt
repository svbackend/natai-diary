package com.svbackend.natai.android.service

import android.net.Uri
import com.svbackend.natai.android.entity.ExistingAttachmentDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository

class AttachmentsLoader(
    private val api: ApiClient,
    private val fm: FileManagerService,
    private val repository: DiaryRepository,
) {
    // res/raw/placeholder.png
    private val placeholderUri: Uri =
        Uri.parse("android.resource://com.svbackend.natai.android/raw/placeholder")

    suspend fun loadAttachments(note: LocalNote): List<ExistingAttachmentDto> {
        val localAttachments = note.attachments.map { attachment ->
            val uri = attachment.uri ?: placeholderUri
            val previewUri = attachment.previewUri ?: placeholderUri
            ExistingAttachmentDto.create(attachment, uri, previewUri)
        }

        val attachmentsWithoutUri = note.attachments
            .filter {it.uri == null || !fm.isFileExists(it.uri) }
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

            return updatedAttachments.map { attachment ->
                val uri = attachment.uri ?: placeholderUri
                val previewUri = attachment.previewUri ?: placeholderUri
                ExistingAttachmentDto.create(attachment, uri, previewUri)
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            return localAttachments
        }
    }
}