package com.svbackend.natai.android.service

import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository
import kotlinx.coroutines.flow.toList

class ApiSyncService(
    private val apiClient: ApiClient,
    private val repository: DiaryRepository
) {
    suspend fun syncNotes() {

        val cloudNotes = apiClient
            .getNotes()
            .associateBy { it.id }

        val localNotes = repository.getAllNotesForSync()

        localNotes.forEach {
            val cloudNote = if (it.cloudId != null) cloudNotes[it.cloudId] else null
            if (cloudNote == null) {
                insertToCloud(it)
            } else if (cloudNote.updatedAt.after(it.updatedAt)) {
                updateToCloud(it)
            }
            // deleteToCloud
        }

        cloudNotes.forEach { kv ->
            val cloudNoteId = kv.key
            val cloudNote = kv.value
            val localNote = localNotes.find { it.cloudId == cloudNoteId }
            if (localNote == null) {
                insertToLocal(cloudNote)
            } else if (cloudNote.updatedAt.after(localNote.updatedAt)) {
                updateToLocal(localNote, cloudNote)
            }
            // todo deleteToLocal
        }
    }

    private suspend fun insertToLocal(cloudNote: Note) {
        val newLocalNote = Note.createByCloudNote(cloudNote)
        try {
            repository.insert(newLocalNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private suspend fun updateToLocal(localNote: Note, cloudNote: Note) {
        localNote.sync(cloudNote)
        try {
            repository.update(localNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun insertToCloud(localNote: Note) {
        try {
            val insertedCloudNote = apiClient.addNote(localNote)
            localNote.cloudId = insertedCloudNote.id
            repository.update(localNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun updateToCloud(localNote: Note) {
        try {
            apiClient.updateNote(localNote)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}