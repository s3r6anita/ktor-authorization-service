package ru.countrystats.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.countrystats.database.UserService.Users

fun Application.configureDatabases(embedded: Boolean = false) {
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")

        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = "",
        )
    } else {
        val url = environment.config.property("ktor.postgres.url").getString()
        log.info("Connecting to postgres database at $url")

        Database.connect(
            url = url,
            user = environment.config.property("ktor.postgres.user").getString(),
            password = environment.config.property("ktor.postgres.password").getString(),
        )
    }

    transaction {
        SchemaUtils.create(Users)
    }
}