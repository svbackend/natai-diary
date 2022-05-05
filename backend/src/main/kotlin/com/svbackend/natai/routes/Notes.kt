package com.svbackend.natai.routes

import com.svbackend.natai.dto.*
import com.svbackend.natai.repository.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.notes(noteRepository: NoteRepository) {
    authenticate("auth0") {
        get {
            val notes = noteRepository.getAllNotes()

            call.respond(notes)
        }

        post {
            val newNote = call.receive<NewNote>()

            noteRepository.createNote(newNote)

            call.response.status(HttpStatusCode.NoContent)
        }
    }
}