package ru.countrystats

import io.ktor.server.application.*
import ru.countrystats.database.configureDatabases
import ru.countrystats.plugin.configureMonitoring
import ru.countrystats.routing.configureRouting
import ru.countrystats.plugin.configureSerialization
import ru.countrystats.security.configureSecurity

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity() // contain jwt
    configureMonitoring() // contain CallLogging
    configureSerialization()
    configureDatabases()
    configureRouting()
}
