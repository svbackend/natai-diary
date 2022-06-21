package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.relation.NoteWithTags
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DiaryRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {
    val notes: Flow<List<LocalNote>> = db
        .dao()
        .getAllNotes()
        .map { notes ->
            notes.map { LocalNote.create(it) }
        }

    suspend fun getAllNotesForSync(): List<LocalNote> = withContext(Dispatchers.IO) {
        db
            .dao()
            .getAllNotesForSync()
            .map { LocalNote.create(it) }
    }

    fun getNote(id: String): Flow<NoteWithTags> {
        return db.dao().getNote(id)
    }

    suspend fun insert(noteEntity: LocalNote) = withContext(Dispatchers.IO) {
        val tags = noteEntity.tags
        val note = Note.create(noteEntity)

        db.dao().insertNote(note)

        tags.forEach {
            insertTag(
                Tag(
                    noteId = note.id,
                    name = it.name,
                    score = it.score,
                )
            )
        }

        if (note.cloudId == null) {
            try {
                val cloudNote = api.addNote(noteEntity) // todo handle http err
                note.sync(cloudNote) // todo handle different ids err
                db.dao().updateNote(note)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        db.dao().updateNote(note)
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        db.dao().deleteNote(note)
    }

    suspend fun insertTag(tag: Tag) = withContext(Dispatchers.IO) {
        db.dao().insertTag(tag)
    }

    suspend fun deleteTagsByNote(noteId: String) = withContext(Dispatchers.IO) {
        db.dao().deleteTagsByNote(noteId)
    }
}