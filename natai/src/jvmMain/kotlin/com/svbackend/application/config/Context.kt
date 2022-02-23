package com.svbackend.application.config

import com.zaxxer.hikari.*
import io.ktor.config.*
import org.jdbi.v3.core.*
import org.jdbi.v3.core.kotlin.*
import org.jdbi.v3.jackson2.*
import org.kodein.di.*

fun context(config: ApplicationConfig): DirectDI = DI.direct {
    import(commonModule(config))
}

fun commonModule(config: ApplicationConfig) = DI.Module("common") {
    bindSingleton {
        val dataSourceConfig = HikariConfig().apply {
            jdbcUrl = config.property("app_db.uri").getString()
        }

        Jdbi.create(HikariDataSource(dataSourceConfig))
            .apply {
                installPlugin(KotlinPlugin())
                installPlugin(Jackson2Plugin())
                getConfig(Jackson2Config::class.java).mapper = instance()
            }
    }
}