package com.svbackend.natai

import com.auth0.jwk.*
import com.fasterxml.jackson.datatype.jsr310.*
import com.svbackend.natai.config.*
import com.svbackend.natai.db.*
import com.svbackend.natai.plugins.*
import com.typesafe.config.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.kodein.di.*
import java.util.concurrent.*

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load("application.conf"))

    val port = config.property("ktor.port").getString().toInt()

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        val di = context(config)
        configurePlugins(di)
        //configureHTTP()
        configureRouting(di)
        configureAuth0(config)
    }.start(wait = true)
}

private fun Application.configureAuth0(config: HoconApplicationConfig) {
    val issuer = config.property("auth0.issuer").getString()
    val audience = config.property("auth0.audience").getString()
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth0") {
            verifier(jwkProvider, issuer)
            validate { credential -> validateCreds(audience, credential) }
        }
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowHeader("authorization")
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
}

private fun Application.configurePlugins(context: DirectDI) {
    install(Compression)
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }

    DatabaseFactory.connectAndMigrate(context.instance())
}

fun validateCreds(audience: String, credential: JWTCredential, permission: String? = null): JWTPrincipal? {
    val containsAudience = credential.payload.audience.contains(audience)
    val containsScope = permission.isNullOrBlank() ||
            credential.payload.claims["permissions"]?.asArray(String::class.java)?.contains(permission) == true

    if (containsAudience && containsScope) {
        return JWTPrincipal(credential.payload)
    }

    return null
}