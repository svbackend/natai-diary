package com.svbackend.application

import com.svbackend.application.config.*
import com.svbackend.application.routes.*
import com.typesafe.config.*
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import org.kodein.di.*

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/natai.js") {}
    }
}

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        val context = context(config)

        configurePlugins(context)

        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            static("/static") {
                resources()
            }
            route("/api") {
                api(context)
            }
        }
    }.start(wait = true)
}

private fun Application.configurePlugins(context: DirectDI) {
    install(Compression)
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(context.instance()))
    }
}