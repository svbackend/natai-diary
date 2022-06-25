package com.svbackend.natai.config

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.*
import com.svbackend.natai.repository.*
import com.zaxxer.hikari.*
import io.ktor.server.config.*
import org.jdbi.v3.core.*
import org.jdbi.v3.core.kotlin.*
import org.jdbi.v3.jackson2.*
import org.kodein.di.*
import javax.sql.DataSource

fun context(config: ApplicationConfig): DirectDI = DI.direct {
    import(commonModule(config))
}

fun commonModule(config: ApplicationConfig) = DI.Module("common") {
    bindSingleton<ObjectMapper> {
        jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            //.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    bindSingleton<DataSource> {
        val dataSourceConfig = HikariConfig().apply {
            jdbcUrl = config.property("app_db.uri").getString()
            isAutoCommit = false
        }

        HikariDataSource(dataSourceConfig)
    }

    bindSingleton {
        Jdbi.create(instance<DataSource>())
            .apply {
                installPlugin(KotlinPlugin())
                installPlugin(Jackson2Plugin())
                getConfig(Jackson2Config::class.java).mapper = instance()
            }
    }

    bindSingleton { NoteRepository(instance(), instance()) }
}