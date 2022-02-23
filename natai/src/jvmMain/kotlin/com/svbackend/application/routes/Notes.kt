package com.svbackend.application.routes.v1

import com.svbackend.application.dto.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.*

fun Route.notes(context: DirectDI) {
    route("") {
        get {

            val notes = listOf(
                NoteDto(id = 1),
                NoteDto(id = 2),
                NoteDto(id = 3),
            )

            call.respond(notes)
        }
    }
}