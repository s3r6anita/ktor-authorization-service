package ru.countrystats.security

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    JwtConfig.initialize()
    authentication {
        jwt("access") {
            verifier(JwtConfig.instance.accessVerifier)
            realm = JwtConfig.realm

            validate { credential ->
                val claim = credential.payload.getClaim(JwtConfig.claim).asString()
                if (claim != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        // Аутентификация для refresh token
        jwt("refresh") {
            verifier(JwtConfig.instance.refreshVerifier) // Используем refreshVerifier
            realm = JwtConfig.realm

            validate { credential ->
                val claim = credential.payload.getClaim(JwtConfig.claim).asString()
                if (claim != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}