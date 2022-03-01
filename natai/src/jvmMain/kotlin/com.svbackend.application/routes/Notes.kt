package com.svbackend.application.routes

import com.svbackend.application.dto.*
import com.svbackend.application.repository.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.*

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