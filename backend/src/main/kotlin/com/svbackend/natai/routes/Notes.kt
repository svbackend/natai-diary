package com.svbackend.natai.routes

import com.svbackend.natai.dto.*
import com.svbackend.natai.repository.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.notes(noteRepository: NoteRepository) {
    get("") {
        val notes = listOf(
            NoteDto(id = 1),
            NoteDto(id = 2),
            NoteDto(id = 3),
        ) + noteRepository.getAllNotes()

        call.respond(notes)
    }

}