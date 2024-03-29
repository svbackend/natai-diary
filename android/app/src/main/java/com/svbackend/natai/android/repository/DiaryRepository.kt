package com.svbackend.natai.android.repository

import android.content.SharedPreferences
import com.svbackend.natai.android.DiaryDatabase
import com.svbackend.natai.android.entity.AttachmentEntityDto
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.relation.NoteWithRelations
import com.svbackend.natai.android.http.ApiClient
import com.svbackend.natai.android.service.AttachmentUris
import com.svbackend.natai.android.utils.isGuest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class DiaryRepository(
    private val db: DiaryDatabase,
    private val api: ApiClient,
    private val prefs: SharedPreferences
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

    suspend fun insertNote(note: LocalNote) = withContext(Dispatchers.IO) {
        db.diaryDAO().insertNote(Note.create(note))
        addTags(note)
        updateAttachments(note.id, note.attachments)
    }

    suspend fun insertNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        insertNote(note)
        syncNoteWithCloud(note)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        db.diaryDAO().updateNote(note)
    }

    suspend fun syncLocalNote(localNote: LocalNote) = withContext(Dispatchers.IO) {
        val note = Note.create(localNote)
        updateNote(note)
        updateTags(localNote)
        updateAttachments(localNote.id, localNote.attachments)
    }

    suspend fun updateNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        updateNote(Note.create(note))
        updateTags(note)
        updateAttachments(note.id, note.attachments)
        syncNoteWithCloud(note)
    }

    suspend fun deleteNoteAndSync(note: LocalNote) = withContext(Dispatchers.IO) {
        val deletedNote = Note.create(note)
        deletedNote.delete()
        updateNote(deletedNote)
        syncNoteWithCloud(note)
    }

    suspend fun insertTag(tag: Tag) = withContext(Dispatchers.IO) {
        db.diaryDAO().insertTag(tag)
    }

    private suspend fun deleteTagsByNote(noteId: String) = withContext(Dispatchers.IO) {
        db.diaryDAO().deleteTagsByNote(noteId)
    }

    suspend fun updateAttachments(localNoteId: String, attachments: List<AttachmentEntityDto>) =
        withContext(Dispatchers.IO) {
            db.diaryDAO().updateAttachments(localNoteId, attachments)
        }

    suspend fun updateTags(note: LocalNote) =
        withContext(Dispatchers.IO) {
            db.diaryDAO().updateTags(note.id, note.tags)
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

    private suspend fun syncNoteWithCloud(note: LocalNote) {
        if (prefs.isGuest()) {
            return
        }

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

    suspend fun getLastNoteByActualDate(actualDate: LocalDate): NoteWithRelations? =
        withContext(Dispatchers.IO) {
            db.diaryDAO().getLastNoteByActualDate(actualDate)
        }

    suspend fun getOldNotes(): List<LocalNote> = withContext(Dispatchers.IO) {
        db.diaryDAO().getOldNotes().map { LocalNote.create(it) }
    }

    suspend fun updateAttachmentsUris(
        note: LocalNote,
        downloadedUrisMap: MutableMap<String, AttachmentUris>
    ): List<AttachmentEntityDto> {
        val updatedAttachments = note.attachments.map { attachment ->
            val uris = downloadedUrisMap[attachment.cloudAttachmentId]

            val currentPreviewUri = attachment.previewUri

            if (uris != null) {
                attachment.copy(
                    uri = uris.uri,
                    previewUri = uris.previewUri ?: currentPreviewUri,
                )
            } else {
                attachment
            }
        }
        updateAttachments(note.id, updatedAttachments)

        return updatedAttachments
    }
}