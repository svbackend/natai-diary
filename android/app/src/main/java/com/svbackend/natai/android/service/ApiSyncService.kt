package com.svbackend.natai.android.service

import android.util.Log
import com.svbackend.natai.android.entity.AttachmentEntityDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.model.CloudAttachment
import com.svbackend.natai.android.http.model.CloudNote
import com.svbackend.natai.android.http.model.PREVIEW_TYPE_MD
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.utils.UtcDateTimeFormatter
import com.svbackend.natai.android.utils.isAfterSecs
import java.time.Instant

class ApiSyncService(
    private val apiClient: ApiClient,
    private val repository: DiaryRepository,
    private val fileManagerService: FileManagerService
) {
    private val TAG = "ApiSyncService"

    @Volatile
    private var isRunning = false

    suspend fun syncNotes(lastSyncTime: Instant? = null) {
        if (isRunning) {
            return
        }

        isRunning = true

        val currentUserResponse = apiClient.getCurrentUser()

        val updatedSince = lastSyncTime ?: Instant.ofEpochSecond(0)
        val cloudNotesResponse = apiClient.getNotesForSync(updatedSince)

        val cloudNotes = cloudNotesResponse
            .notes
            .associateBy { it.id }

        val allLocalNotes = repository
            .getAllNotesForSync()

        val localNotes = allLocalNotes
            .filter { it.cloudUserId == null || it.cloudUserId == currentUserResponse.user.id.toString() }

        val localNotesUpdated = localNotes
            .filter { it.updatedAt.isAfterSecs(updatedSince) }

        Log.v(TAG, "=== SYNC STARTED ===")
        Log.v(TAG, "=== UPDATE SINCE ${UtcDateTimeFormatter.backendDateTime.format(updatedSince)} ===")

        localNotesUpdated.forEach {
            val cloudNote = if (it.cloudId != null) cloudNotes[it.cloudId] else null

            Log.v(TAG, "=== LOCAL NOTE ===")
            Log.v(TAG, it.toString())

            Log.v(TAG, "=== CLOUD NOTE ===")
            Log.v(TAG, cloudNote.toString())

            if (it.cloudId == null) {
                insertToCloud(it)
            } else if (cloudNote == null || it.updatedAt.isAfterSecs(cloudNote.updatedAt)) {
                updateToCloud(it)
            }
        }

        cloudNotes.forEach { kv ->
            val cloudNoteId = kv.key
            val cloudNote = kv.value
            val localNote = localNotes.find { it.cloudId == cloudNoteId }

            Log.v(TAG, "=== STAGE2 LOCAL NOTE ===")
            Log.v(TAG, localNote.toString())

            Log.v(TAG, "=== STAGE2 CLOUD NOTE ===")
            Log.v(TAG, cloudNote.toString())

            if (localNote == null) {
                insertToLocal(cloudNote)
            } else if (cloudNote.updatedAt.isAfterSecs(localNote.updatedAt)) {
                updateToLocal(localNote, cloudNote)
            }
        }

        //cleanOldAttachments()

        isRunning = false
    }

    private suspend fun insertToLocal(cloudNote: CloudNote) {
        Log.v(TAG, "=== INSERT TO LOCAL ===")
        var newLocalNote = LocalNote.create(cloudNote)

        if (newLocalNote.attachments.isNotEmpty()) {
            try {
                val response = apiClient.getAttachmentsByNote(cloudNote.id)
                val attachments = processAttachments(response.attachments)
                newLocalNote = newLocalNote.updateAttachments(attachments)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        try {
            repository.insertNote(newLocalNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private suspend fun updateToLocal(localNote: LocalNote, cloudNote: CloudNote) {
        Log.v(TAG, "=== UPDATE TO LOCAL ===")

        val response = apiClient.getAttachmentsByNote(cloudNote.id)
        val attachments = processAttachments(response.attachments, localNote.attachments)

        val updatedLocalNote = localNote.update(
            title = cloudNote.title,
            content = cloudNote.content,
            actualDate = cloudNote.actualDate,
            tags = cloudNote.tags.map { TagEntityDto(it.tag, it.score) },
            attachments = attachments,
        )

        Log.v(TAG, "=== UPDATE LOCAL NOTE ===")
        Log.v(TAG, updatedLocalNote.toString())

        Log.v(TAG, "=== processAttachments ===")
        Log.v(TAG, attachments.toString())

        try {
            repository.syncLocalNote(updatedLocalNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun insertToCloud(localNote: LocalNote) {
        Log.v(TAG, "=== INSERT TO CLOUD ===")
        try {
            val attachments = fileManagerService.uploadAttachments(localNote.attachments)
            val noteWithAttachments = localNote.updateAttachments(attachments)
            val response = apiClient.addNote(noteWithAttachments)
            val syncedNote = Note.create(noteWithAttachments)
            syncedNote.cloudId = response.noteId

            repository.updateNote(syncedNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun updateToCloud(localNote: LocalNote) {
        Log.v(TAG, "=== UPDATE TO CLOUD ===")
        try {
            val attachments = fileManagerService.uploadAttachments(localNote.attachments)
            val noteWithAttachments = localNote.updateAttachments(attachments)
            apiClient.updateNote(noteWithAttachments)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    // download previews + update original filenames
    private suspend fun processAttachments(
        cloudAttachments: List<CloudAttachment>,
        localAttachments: List<AttachmentEntityDto> = emptyList()
    ): List<AttachmentEntityDto> {
        val cacheDir = fileManagerService.cacheDir

        return cloudAttachments.map { cloudAttachment ->
            val mediumPreview = cloudAttachment.previews.find { it.type == PREVIEW_TYPE_MD }

            val localAttachment = localAttachments.find { it.cloudAttachmentId == cloudAttachment.attachmentId }
            val localUri = localAttachment?.uri
            val localPreviewUri = localAttachment?.previewUri

            val dtoWithoutPreview = AttachmentEntityDto(
                uri = localUri,
                previewUri = localPreviewUri,
                filename = cloudAttachment.originalFilename,
                cloudAttachmentId = cloudAttachment.attachmentId
            )

            if (mediumPreview == null) {
                return@map dtoWithoutPreview
            }

            try {
                val tmpUri = apiClient.downloadAttachment(cacheDir, mediumPreview.signedUrl)
                val localStorageUri = fileManagerService.savePreview(
                    uri = tmpUri,
                    originalFilename = cloudAttachment.originalFilename
                )

                return@map AttachmentEntityDto(
                    uri = null,
                    previewUri = localStorageUri,
                    filename = cloudAttachment.originalFilename,
                    cloudAttachmentId = cloudAttachment.attachmentId,
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                return@map dtoWithoutPreview
            }
        }
    }

    private suspend fun cleanOldAttachments() {
        // remove all attachments files for 10th+ note
        val oldNotes = repository.getOldNotes()

        oldNotes.forEach {
            val newAttachments = mutableListOf<AttachmentEntityDto>()
            var isChanged = false
            it.attachments.forEach { attachment ->
                if (attachment.uri != null) {
                    fileManagerService.deleteFile(attachment.uri)
                    newAttachments.add(attachment.copy(uri = null))
                    isChanged = true
                } else {
                    newAttachments.add(attachment)
                }
            }

            if (isChanged) {
                repository.updateAttachments(it.id, newAttachments)
            }
        }
    }
}