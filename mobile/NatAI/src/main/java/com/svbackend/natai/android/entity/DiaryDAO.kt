package com.svbackend.natai.android.entity

import androidx.room.*
import com.svbackend.natai.android.entity.relation.NoteWithTags
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiaryDAO {
    @Query("SELECT * FROM Note WHERE deletedAt IS NULL ORDER BY date(actualDate) DESC")
    abstract fun getNotes(): Flow<List<NoteWithTags>>

    @Query("SELECT * FROM Note ORDER BY date(actualDate) DESC")
    abstract fun getAllNotesForSync(): List<NoteWithTags>

    @Query("SELECT * FROM Note WHERE id = :id")
    abstract fun getNote(id: String): Flow<NoteWithTags>

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

    @Query("DELETE FROM Tag WHERE noteId = :noteId")
    abstract fun deleteTagsByNote(noteId: String)

    @Query("UPDATE Note SET cloudUserId = :cloudUserId WHERE cloudUserId IS NULL")
    abstract fun assignNotesToNewUser(cloudUserId: String)
}