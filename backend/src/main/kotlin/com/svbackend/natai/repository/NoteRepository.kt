package com.svbackend.natai.repository

import com.svbackend.natai.db.DatabaseFactory.dbQuery
import com.svbackend.natai.dto.*
import com.svbackend.natai.entity.*
import org.jdbi.v3.core.*
import org.jetbrains.exposed.sql.*
import java.time.*

class NoteRepository(private val jdbi: Jdbi) {
    fun getAllNotes(): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery("SELECT * FROM notes WHERE deleted_at IS NULL ORDER BY created_at DESC")
            .map { rs, _ ->
                NoteDto(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    createdAt = rs.getDate("created_at"),
                    updatedAt = rs.getDate("updated_at"),
                )
            }.list()
    }

    fun getAllNotesForSync(): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery("SELECT * FROM notes ORDER BY created_at DESC")
            .map { rs, _ ->
                NoteDto(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    createdAt = rs.getDate("created_at"),
                    updatedAt = rs.getDate("updated_at"),
                    deletedAt = rs.getDate("deleted_at"),
                )
            }.list()
    }

    suspend fun createNote(note: NewNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt.toInstant(), ZoneId.systemDefault())
        dbQuery {
            Notes.insert {
                it[id] = note.id
                it[title] = note.title
                it[content] = note.content
                it[userId] = note.userId
                it[deletedAt] = deletedDateTime
            }
        }
    }

    suspend fun updateNote(note: UpdateNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt.toInstant(), ZoneId.systemDefault())

        dbQuery {
            Notes.update({ Notes.id eq note.id and (Notes.userId eq note.userId) })  {
                it[title] = note.title
                it[content] = note.content
                it[deletedAt] = deletedDateTime
                it[updatedAt] = LocalDateTime.now()
            }

//            if (updatedRecords != 1) {
//                // todo throw or return err
//            }
        }
    }
}

