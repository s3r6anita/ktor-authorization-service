package ru.countrystats.plugin

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import ru.countrystats.util.LocalDateTimeAdapter
import java.time.LocalDateTime

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        }
    }
}
