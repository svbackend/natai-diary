package com.svbackend.natai

import com.svbackend.natai.config.*
import com.svbackend.natai.db.*
import com.svbackend.natai.plugins.*
import com.typesafe.config.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import org.kodein.di.*

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val di = context(config)
        configurePlugins(di)
        //configureHTTP()
        configureRouting(di)
    }.start(wait = true)
}

private fun Application.configurePlugins(context: DirectDI) {
    install(Compression)
    install(ContentNegotiation) {
        jackson()
    }
    DatabaseFactory.connectAndMigrate(context.instance())
}