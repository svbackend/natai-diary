package com.svbackend.application.routes.v1

import io.ktor.routing.*
import org.kodein.di.*

fun Route.v1(context: DirectDI) {
    route("/notes") {
        notes(context)
    }
}