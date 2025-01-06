package ru.countrystats

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import ru.countrystats.database.configureDatabases
import ru.countrystats.plugin.configureMonitoring
import ru.countrystats.plugin.configureSerialization
import ru.countrystats.plugin.envConfig
import ru.countrystats.routing.configureRouting
import ru.countrystats.security.configureSecurity

fun main(args: Array<String>) {
    val config = HoconApplicationConfig(ConfigFactory.load())
    embeddedServer(
        factory = Netty,
        environment = applicationEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
        },
        configure = { envConfig(config) },
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    configureSecurity() // contain jwt
    configureMonitoring() // contain CallLogging
    configureSerialization()
    configureDatabases(config)
    configureRouting(config)
}
