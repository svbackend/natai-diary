package com.svbackend.natai.android.entity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiaryDAO {
    @Query("SELECT * FROM Note WHERE deletedAt IS NULL ORDER BY date(actualDate) DESC")
    abstract fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note ORDER BY date(actualDate) DESC")
    abstract fun getAllNotesForSync(): List<Note>

    @Query("SELECT * FROM Note WHERE id = :id")
    abstract fun getNote(id: String): Flow<Note>

    @Insert
    abstract fun insert(note: Note)

    @Update
    abstract fun update(note: Note)

    @Delete
    abstract fun delete(note: Note)
}