package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {

    val notes: Flow<List<Note>> = db.dao().getAllNotes()

    suspend fun getAllNotesForSync(): List<Note> = withContext(Dispatchers.IO) {
        db.dao().getAllNotesForSync()
    }

    fun getNote(id: String): Flow<Note> {
        return db.dao().getNote(id)
    }

    suspend fun insert(note: Note) = withContext(Dispatchers.IO) {
        db.dao().insert(note)

        if (note.cloudId == null) {
            try {
                val cloudNote = api.addNote(note) // todo handle http err
                note.sync(cloudNote) // todo handle different ids err
                db.dao().update(note)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        db.dao().update(note)
    }

    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        db.dao().delete(note)
    }
}