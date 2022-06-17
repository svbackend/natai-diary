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
                    actualDate = rs.getDate("actual_date").toLocalDate(),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                )
            }.list()
    }

    fun getAllNotesForSync(userId: String): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery("SELECT * FROM notes WHERE user_id = :userId ORDER BY created_at DESC")
            .bind("userId", userId)
            .map { rs, _ ->
                NoteDto(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    actualDate = rs.getDate("actual_date").toLocalDate(),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                    deletedAt = rs.getTimestamp("deleted_at")?.toInstant(),
                )
            }.list()
    }

    suspend fun createNote(note: NewNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt, ZoneId.systemDefault())
        dbQuery {
            Notes.insert {
                it[id] = note.id
                it[title] = note.title
                it[content] = note.content
                it[actualDate] = note.actualDate
                it[userId] = note.userId
                it[deletedAt] = deletedDateTime
            }
        }
    }

    suspend fun updateNote(note: UpdateNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt, ZoneId.systemDefault())

        dbQuery {
            Notes.update({ Notes.id eq note.id and (Notes.userId eq note.userId) })  {
                it[title] = note.title
                it[content] = note.content
                it[actualDate] = note.actualDate
                it[deletedAt] = deletedDateTime
                it[updatedAt] = LocalDateTime.now()
            }

//            if (updatedRecords != 1) {
//                // todo throw or return err
//            }
        }
    }
}

