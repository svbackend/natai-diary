package com.svbackend.natai.repository

import com.svbackend.natai.dto.*
import org.jdbi.v3.core.Jdbi

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
}