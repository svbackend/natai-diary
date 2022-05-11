package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient,
) : IDiaryRepository {


    override val notes: Flow<List<Note>> = db.dao().getAllNotes()

    override suspend fun getNote(id: String): Note = withContext(Dispatchers.IO) {
        db.dao().getNote(id)
    }

    override suspend fun insert(note: Note) = withContext(Dispatchers.IO) {
        db.dao().insert(note)
        try {
            api.addNote(note)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        db.dao().update(note)
    }

    override suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        db.dao().delete(note)
    }
}