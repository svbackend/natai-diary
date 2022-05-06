package com.svbackend.natai.repository

import com.svbackend.natai.db.DatabaseFactory.dbQuery
import com.svbackend.natai.dto.*
import com.svbackend.natai.entity.*
import org.jdbi.v3.core.*
import org.jetbrains.exposed.sql.*

class NoteRepository(private val jdbi: Jdbi) {
    fun getAllNotes(): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery("SELECT * FROM notes ORDER BY created_at DESC")
            .map { rs, _ ->
                NoteDto(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content")
                )
            }.list()
    }

    suspend fun createNote(note: NewNote) {
        dbQuery {
            val id = (Notes.insert {
                it[title] = note.title
                it[content] = note.content
                it[userId] = note.userId
            } get Notes.id)
        }
    }
}

