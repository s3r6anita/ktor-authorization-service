package ru.countrystats.plugin

import io.ktor.network.tls.certificates.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import java.io.File

fun ApplicationEngine.Configuration.envConfig(config: ApplicationConfig) {

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = config.property("ktor.security.ssl.privateKeyPassword").getString()
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, config.property("ktor.security.ssl.keyStorePassword").getString())

// раскомментировать для использования http
//    connector {
//        port = config.property("ktor.deployment.port").getString().toInt()
//    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { config.property("ktor.security.ssl.keyStorePassword").getString().toCharArray() },
        privateKeyPassword = { config.property("ktor.security.ssl.privateKeyPassword").getString().toCharArray() }) {
        port = config.property("ktor.deployment.sslPort").getString().toInt()
        keyStorePath = keyStoreFile
    }
}