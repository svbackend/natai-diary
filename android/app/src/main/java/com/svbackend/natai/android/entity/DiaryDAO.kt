package com.svbackend.natai.android.entity

import android.util.Log
import androidx.room.*
import com.svbackend.natai.android.entity.relation.NoteWithRelations
import com.svbackend.natai.android.entity.relation.NoteWithTags
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

@Dao
abstract class DiaryDAO {
    val TAG = "DiaryDAO"
    @Query("SELECT * FROM Note WHERE deletedAt IS NULL ORDER BY date(actualDate) DESC")
    abstract fun getNotes(): Flow<List<NoteWithRelations>>

    @Query("SELECT * FROM Note ORDER BY date(actualDate) DESC")
    abstract fun getAllNotesForSync(): List<NoteWithRelations>

    @Query("SELECT * FROM Note WHERE actualDate = :actualDate AND deletedAt IS NULL ORDER BY createdAt DESC LIMIT 1")
    abstract fun getLastNoteByActualDate(actualDate: LocalDate): NoteWithRelations?

    @Query("SELECT * FROM Note WHERE id = :id")
    abstract fun getNote(id: String): Flow<NoteWithRelations>

    @Insert
    abstract fun insertNote(note: Note)

    @Update
    abstract fun updateNote(note: Note)

    @Delete
    abstract fun deleteNote(note: Note)

    @Insert
    abstract fun insertTag(tag: Tag)

    @Delete
    abstract fun deleteTag(tag: Tag)

    @Insert
    abstract fun insertAttachment(attachment: Attachment)

    @Query("DELETE FROM Tag WHERE noteId = :noteId")
    abstract fun deleteTagsByNote(noteId: String)

    @Query("DELETE FROM Attachment WHERE noteId = :noteId")
    abstract fun deleteAttachmentsByNote(noteId: String)

    @Query("UPDATE Note SET cloudUserId = :cloudUserId WHERE cloudUserId IS NULL")
    abstract fun assignNotesToNewUser(cloudUserId: String)

    @Query("SELECT * FROM Note ORDER BY date(actualDate) DESC LIMIT -1 OFFSET 10")
    abstract fun getOldNotes(): List<NoteWithRelations>

    @Transaction
    open fun updateAttachments(localNoteId: String, attachments: List<AttachmentEntityDto>) {
        deleteAttachmentsByNote(localNoteId)
        attachments.forEach { dto ->
            insertAttachment(
                Attachment.create(noteId = localNoteId, dto = dto)
            )
        }
    }

    @Transaction
    open fun updateTags(localNoteId: String, tags: List<TagEntityDto>) {
        deleteTagsByNote(localNoteId)
        tags.forEach {
            insertTag(
                Tag(
                    noteId = localNoteId,
                    tag = it.tag,
                    score = it.score,
                )
            )
        }

        Log.v(TAG, "Updated tags for note $localNoteId")
    }
}