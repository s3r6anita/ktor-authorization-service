package ru.countrystats.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.util.*


class JwtConfig private constructor() {

    private val accessAlgorithm = Algorithm.HMAC512(secret)
    private val refreshAlgorithm = Algorithm.HMAC512(refreshSecret) // Секрет для refresh token

    val accessVerifier: JWTVerifier =
        JWT
            .require(accessAlgorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    val refreshVerifier: JWTVerifier =
        JWT
            .require(refreshAlgorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun generateAccessToken(email: String): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(claim, email)
            .withExpiresAt(getAccessExpirationTime())
            .sign(accessAlgorithm)

    fun generateRefreshToken(email: String): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(claim, email)
            .withExpiresAt(getRefreshExpirationTime())
            .sign(refreshAlgorithm)


    private fun getAccessExpirationTime() = Date(System.currentTimeMillis() + expirationPeriod)
    private fun getRefreshExpirationTime() = Date(System.currentTimeMillis() + refreshExpirationPeriod)

    companion object {
        private val config = HoconApplicationConfig(ConfigFactory.load())
        val realm = config.property("ktor.security.jwt.realm").getString()
        val secret = config.property("ktor.security.jwt.secret").getString()
        val refreshSecret = config.property("ktor.security.jwt.refreshSecret").getString()
        val issuer = config.property("ktor.security.jwt.issuer").getString()
        val audience = config.property("ktor.security.jwt.audience").getString()
        val claim = config.property("ktor.security.jwt.claim").getString()
        val expirationPeriod = config.property("ktor.security.jwt.expiration_time").getString().toLong()
        val refreshExpirationPeriod = config.property("ktor.security.jwt.refresh_expiration_time").getString().toLong()

        lateinit var instance: JwtConfig
            private set

        fun initialize() {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = JwtConfig()
                }
            }
        }
    }
}



