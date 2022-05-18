package com.svbackend.natai.android.service

import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.repository.DiaryRepository

class ApiSyncService(
    private val apiClient: ApiClient,
    private val repository: DiaryRepository
) {
    suspend fun syncNotes() {

        val cloudNotes = apiClient
            .getNotes()
            .associateBy { it.id }

        repository.notes.collect { notes ->
            notes.forEach {
                val cloudNote = if (it.cloudId != null) cloudNotes[it.cloudId] else null
                if (cloudNote == null) {
                    insertToCloud(it)
                } else if (cloudNote.updatedAt.after(it.updatedAt)) {
                    updateToCloud(it)
                }
                // todo insertToLocal, updateToLocal, deleteToCloud, deleteToLocal
            }
        }
    }

    private suspend fun insertToCloud(localNote: Note) {
        try {
            val insertedCloudNote = apiClient.addNote(localNote)
            localNote.cloudId = insertedCloudNote.id
            repository.update(localNote)
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        }
    }

    private suspend fun updateToCloud(localNote: Note) {
        try {
            apiClient.updateNote(localNote)
        } catch (e: Throwable) {
            println(e.stackTraceToString())
        }
    }
}