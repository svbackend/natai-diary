package com.svbackend.application.routes

import com.svbackend.application.routes.v1.*
import io.ktor.routing.*
import org.kodein.di.*

fun Route.api(context: DirectDI) {
    route("/v1") {
        v1(context)
    }
}