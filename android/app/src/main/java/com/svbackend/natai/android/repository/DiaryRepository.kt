package com.svbackend.natai.android.repository

import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.Attachment
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.relation.NoteWithRelations
import com.svbackend.natai.android.entity.relation.NoteWithTags
import com.svbackend.natai.android.http.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class DiaryRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient
) {
    val notes: Flow<List<LocalNote>> = db
        .diaryDAO()
        .getNotes()
        .map { notes ->
            notes.map { LocalNote.create(it) }
        }

    suspend fun getAllNotesForSync(): List<LocalNote> = withContext(Dispatchers.IO) {
        db
            .diaryDAO()
            .getAllNotesForSync()
            .map { LocalNote.create(it) }
    }

    fun getNote(id: String): Flow<NoteWithRelations> {
        return db.diaryDAO().getNote(id)
    }

    suspend fun insertNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        db.diaryDAO().insertNote(Note.create(note))
        addTags(note)
        addAttachments(note)
        syncNoteWithCloud(note)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        db.diaryDAO().updateNote(note)
    }

    suspend fun updateNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        updateNote(Note.create(note))

        deleteTagsByNote(note.id)
        addTags(note)

        deleteAttachmentsByNote(note.id)
        addAttachments(note)

        syncNoteWithCloud(note)
    }

    suspend fun deleteNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        val deletedNote = Note.create(note)
        deletedNote.deletedAt = Instant.now()
        updateNote(deletedNote)
        syncNoteWithCloud(note)
    }

    suspend fun insertTag(tag: Tag) = withContext(Dispatchers.IO) {
        db.diaryDAO().insertTag(tag)
    }

    suspend fun insertAttachment(attachment: Attachment) = withContext(Dispatchers.IO) {
        db.diaryDAO().insertAttachment(attachment)
    }

    private suspend fun deleteTagsByNote(noteId: String) = withContext(Dispatchers.IO) {
        db.diaryDAO().deleteTagsByNote(noteId)
    }

    private suspend fun deleteAttachmentsByNote(noteId: String) = withContext(Dispatchers.IO) {
        db.diaryDAO().deleteAttachmentsByNote(noteId)
    }

    private suspend fun addTags(note: LocalNote) {
        note.tags.forEach {
            insertTag(
                Tag(
                    noteId = note.id,
                    tag = it.tag,
                    score = it.score,
                )
            )
        }
    }

    private suspend fun addAttachments(note: LocalNote) {
        note.attachments.forEach {
            insertAttachment(
                Attachment(
                    noteId = note.id,
                    cloudAttachmentId = it.cloudAttachmentId,
                    uri = it.uri.toString(),
                    filename = it.filename,
                )
            )
        }
    }

    private suspend fun syncNoteWithCloud(note: LocalNote) {
        try {
            if (note.cloudId != null) {
                api.updateNote(note)
            } else {
                val response = api.addNote(note)
                val syncedLocalNote = note.cloneWithCloudId(response.noteId)
                updateNote(Note.create(syncedLocalNote))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun assignNotesToUser(cloudUserId: String) = withContext(Dispatchers.IO) {
        db.diaryDAO().assignNotesToNewUser(cloudUserId = cloudUserId)
    }

    suspend fun getLastNoteByActualDate(actualDate: LocalDate): NoteWithRelations? = withContext(Dispatchers.IO) {
        db.diaryDAO().getLastNoteByActualDate(actualDate)
    }
}