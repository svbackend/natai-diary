package com.svbackend.natai.android.service

import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.http.model.CloudNote
import com.svbackend.natai.android.query.UserQueryException
import com.svbackend.natai.android.repository.DiaryRepository
import com.svbackend.natai.android.utils.LocalDateTimeFormatter

class ApiSyncService(
    private val apiClient: ApiClient,
    private val repository: DiaryRepository,
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

        localNotes.forEach {
            val cloudNote = if (it.cloudId != null) cloudNotes[it.cloudId] else null
            if (cloudNote == null) {
                insertToCloud(it)
            } else if (it.updatedAt.isAfter(cloudNote.updatedAt)) {
                println(LocalDateTimeFormatter.fullDateTime.format(it.updatedAt))
                println("vs")
                println(LocalDateTimeFormatter.fullDateTime.format(cloudNote.updatedAt))
                updateToCloud(it)
            }
        }

        cloudNotes.forEach { kv ->
            val cloudNoteId = kv.key
            val cloudNote = kv.value
            val localNote = localNotes.find { it.cloudId == cloudNoteId }
            if (localNote == null) {
                insertToLocal(cloudNote)
            } else if (cloudNote.updatedAt.isAfter(localNote.updatedAt)) {
                updateToLocal(localNote, cloudNote)
            }
        }

        isRunning = false
    }

    private suspend fun insertToLocal(cloudNote: CloudNote) {
        val newLocalNote = LocalNote.create(cloudNote)
        try {
            repository.insertNoteAndSync(newLocalNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private suspend fun updateToLocal(localNote: LocalNote, cloudNote: CloudNote) {
        val note = Note.create(localNote)
        note.sync(cloudNote)
        try {
            repository.updateNote(note)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun insertToCloud(localNote: LocalNote) {
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
        try {
            apiClient.updateNote(localNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}