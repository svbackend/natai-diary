package com.svbackend.natai.android.entity

import androidx.room.*
import com.svbackend.natai.android.entity.relation.NoteWithRelations
import com.svbackend.natai.android.entity.relation.NoteWithTags
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class DiaryDAO {
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
}