package com.svbackend.natai.plugins

import com.svbackend.natai.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.*

fun Application.configureRouting(context: DirectDI) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/api") {
            api(context)
        }
    }
}
