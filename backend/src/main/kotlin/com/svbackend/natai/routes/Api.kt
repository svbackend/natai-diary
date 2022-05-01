package com.svbackend.natai.routes

import com.svbackend.natai.routes.v1.*
import io.ktor.server.routing.*
import org.kodein.di.*

fun Route.api(context: DirectDI) {
    route("/v1") {
        v1(context)
    }
}