package com.svbackend.natai.routes

import com.svbackend.natai.dto.*
import com.svbackend.natai.repository.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.notes(noteRepository: NoteRepository) {
    authenticate("auth0") {
        get {
            val notes = noteRepository.getAllNotes()

            call.respond(notes)
        }

        post {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("sub").asString()

            val draft = call.receive<NewNoteDraft>()

            val newNote = NewNote(
                id = UUID.randomUUID(),
                userId = userId,
                title = draft.title,
                content = draft.content
            )

            noteRepository.createNote(newNote)

            call.response.status(HttpStatusCode.Created)
            call.respond(newNote)
        }
    }
}