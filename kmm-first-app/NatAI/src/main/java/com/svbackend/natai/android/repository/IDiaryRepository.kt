package com.svbackend.natai.android.repository


import com.svbackend.natai.android.entity.Note
import kotlinx.coroutines.flow.Flow

interface IDiaryRepository {
    val notes: Flow<List<Note>>

    suspend fun loadNotes()

    suspend fun getNote(id: String): Note

    suspend fun insert(note: Note)

    suspend fun update(note: Note)

    suspend fun delete(note: Note)
}