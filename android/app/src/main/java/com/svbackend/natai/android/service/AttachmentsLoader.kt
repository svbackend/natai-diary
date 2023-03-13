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
        println("loadAttachments")
        println(note.cloudId)
        println(note.attachments)

        val localAttachments = note.attachments.map { attachment ->
            val uri = attachment.uri ?: placeholderUri
            val previewUri = attachment.previewUri ?: placeholderUri
            ExistingAttachmentDto.create(attachment, uri, previewUri)
        }

        val attachmentsWithoutUri = note.attachments
            .filter { it.uri == null || !fm.isFileExists(it.uri) }
            .mapNotNull { it.cloudAttachmentId }

        val attachmentsWithoutPreview = note.attachments
            .filter { it.previewUri == null || !fm.isFileExists(it.previewUri) }
            .mapNotNull { it.cloudAttachmentId }

        if (note.cloudId == null) {
            // nothing to load
            return localAttachments
        }

        try {
            val cloudAttachments = api
                .getAttachmentsByNote(note.cloudId, attachmentsWithoutUri)
                .attachments

            println(cloudAttachments)

            val downloadedUrisMap = mutableMapOf<String, Uri>() // cloudAttachmentId to uri

            attachmentsWithoutUri.forEach {
                val cloudAttachment = cloudAttachments.find { cloudAttachment ->
                    cloudAttachment.attachmentId == it
                }

                if (cloudAttachment != null) {
                    val uri = api.downloadAttachment(fm.filesDir, cloudAttachment.signedUrl, cloudAttachment.filename)
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