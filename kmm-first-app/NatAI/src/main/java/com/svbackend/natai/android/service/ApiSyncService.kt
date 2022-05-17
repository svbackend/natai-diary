package com.svbackend.natai.android.service

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
                val cloudNote = cloudNotes[it.cloudId]
                if (cloudNote == null) {
                    // todo sync
                }
            }
        }
    }
}