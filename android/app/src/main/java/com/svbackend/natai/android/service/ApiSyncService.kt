package com.svbackend.natai.android.service

import com.svbackend.natai.android.entity.AttachmentEntityDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.model.CloudNote
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.utils.isAfterSecs

class ApiSyncService(
    private val apiClient: ApiClient,
    private val repository: DiaryRepository,
    private val fileManagerService: FileManagerService
) {
    @Volatile
    private var isRunning = false

    suspend fun syncNotes() {
        if (isRunning) {
            return
        }

        isRunning = true

        apiClient.getCurrentUser()

        val cloudNotesResponse = apiClient.getNotesForSync()

        val cloudNotes = cloudNotesResponse
            .notes
            .associateBy { it.id }

        val localNotes = repository
            .getAllNotesForSync()

        println("[SYNC] STARTED")

        localNotes.forEach {
            val cloudNote = if (it.cloudId != null) cloudNotes[it.cloudId] else null

            println("[SYNC] LOCAL NOTE / CLOUD NOTE")
            println(it)
            println(cloudNote)

            if (cloudNote == null) {
                insertToCloud(it)
            } else if (it.updatedAt.isAfterSecs(cloudNote.updatedAt)) {
                println("[SYNC] updateToCloud")
                println("[SYNC] ${it.updatedAt} is after ${cloudNote.updatedAt}")
                updateToCloud(it)
            }
        }

        cloudNotes.forEach { kv ->
            val cloudNoteId = kv.key
            val cloudNote = kv.value
            val localNote = localNotes.find { it.cloudId == cloudNoteId }

            println("[SYNC] LOCAL NOTE / CLOUD NOTE (2)")
            println(localNote)
            println(cloudNote)

            if (localNote == null) {
                insertToLocal(cloudNote)
            } else if (cloudNote.updatedAt.isAfterSecs(localNote.updatedAt)) {
                println("[SYNC] updateToLocal")
                println("[SYNC] ${cloudNote.updatedAt} is after ${localNote.updatedAt}")
                updateToLocal(localNote, cloudNote)
            }
        }

        cleanOldAttachments()

        isRunning = false
    }

    private suspend fun insertToLocal(cloudNote: CloudNote) {
        val newLocalNote = LocalNote.create(cloudNote)
        println("[SYNC] insertToLocal")
        println(newLocalNote)
        try {
            repository.insertNoteAndSync(newLocalNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private suspend fun updateToLocal(localNote: LocalNote, cloudNote: CloudNote) {
        val note = Note.create(localNote)
        note.sync(cloudNote)

        println("[SYNC] updateToLocal")
        println(note)

        val attachments = cloudNote.attachments.map {
            AttachmentEntityDto.create(it)
        }

        try {
            repository.updateNote(note)
            repository.updateAttachments(note.id, attachments)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun insertToCloud(localNote: LocalNote) {
        println("[SYNC] insertToCloud")
        println(localNote)

        try {
            val response = apiClient.addNote(localNote)
            val syncedNote = Note.create(localNote)
            syncedNote.cloudId = response.noteId

            repository.updateNote(syncedNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun updateToCloud(localNote: LocalNote) {
        println("[SYNC] updateToCloud")
        println(localNote)

        try {
            apiClient.updateNote(localNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun cleanOldAttachments() {
        // remove all attachments files for 10th+ note
        val oldNotes = repository.getOldNotes()

        println("-============ OLD NOTES SIZE: ")
        println(oldNotes.size)

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