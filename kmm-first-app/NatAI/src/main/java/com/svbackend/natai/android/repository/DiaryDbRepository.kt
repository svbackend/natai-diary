package com.svbackend.natai.android.repository

import android.app.Application
import androidx.room.Room
import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryDbRepository(app: Application) : IDiaryRepository {
    private val db = Room
        .databaseBuilder(app, DiaryDatabase::class.java, "DIARY")
        .build()

    override val notes: Flow<List<Note>> = db.dao().getAllNotes()

    override suspend fun getNote(id: String): Note = withContext(Dispatchers.IO) {
        db.dao().getNote(id)
    }

    override suspend fun insert(note: Note) = withContext(Dispatchers.IO) {
        db.dao().insert(note)
    }

    override suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        db.dao().update(note)
    }

    override suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        db.dao().delete(note)
    }
}