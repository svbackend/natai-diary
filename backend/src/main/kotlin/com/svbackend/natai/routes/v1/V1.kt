package com.svbackend.natai.routes.v1

import com.svbackend.natai.routes.*
import io.ktor.server.routing.*
import org.kodein.di.*

fun Route.v1(context: DirectDI) {
    route("/notes") {
        notes(context.instance())
    }
}