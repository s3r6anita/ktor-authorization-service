package ru.countrystats.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.countrystats.routing.routes.authRoutes

fun Application.configureRouting() {
    routing {
        authRoutes()
    }
}

