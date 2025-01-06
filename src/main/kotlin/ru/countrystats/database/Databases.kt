package ru.countrystats.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
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
        Database.connect(
            driver = environment.config.property("ktor.postgres.driverClassName").getString(),
            url = environment.config.property("ktor.postgres.url").getString(),
            user = environment.config.property("ktor.postgres.user").getString(),
            password = environment.config.property("ktor.postgres.password").getString(),
        )
//        Database.connect(HikariDataSource(HikariConfig().apply {
//            driverClassName = environment.config.property("ktor.postgres.driverClassName").getString()
//            jdbcUrl = environment.config.property("ktor.postgres.url").getString()
//            username = environment.config.property("ktor.postgres.user").getString()
//            password = environment.config.property("ktor.postgres.password").getString()
//            maximumPoolSize = 3
//            isAutoCommit = false
//            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
//            validate()
//        }))
    }
    transaction {
        SchemaUtils.create(Users)
    }
}
