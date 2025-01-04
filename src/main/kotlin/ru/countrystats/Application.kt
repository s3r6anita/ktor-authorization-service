package ru.countrystats

import io.ktor.server.application.*
import ru.countrystats.database.configureDatabases
import ru.countrystats.jwt.configureSecurity
import ru.countrystats.plugin.configureRouting
import ru.countrystats.plugin.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    configureSecurity() // contain jwt
//    configureMonitoring() // contain CallLogging
    configureSerialization()
    configureDatabases()
    configureRouting()
}
