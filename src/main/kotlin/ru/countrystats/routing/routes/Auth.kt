package ru.countrystats.routing.routes

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.countrystats.database.model.LoginUserParams
import ru.countrystats.database.model.RegisterUserParams
import ru.countrystats.repository.IUserRepository
import ru.countrystats.repository.UserRepository

fun Route.authRoutes(
    config: ApplicationConfig,
    repository: IUserRepository = UserRepository(),
    claim: String = config.property("ktor.security.jwt.claim").getString()
) {
    post("/register") {
        val params = call.receive<RegisterUserParams>()
        val result = repository.registerUser(params)
        call.respond(result.statusCode, result)
    }
    post("/login") {
        val params = call.receive<LoginUserParams>()
        val result = repository.loginUser(params)
        call.respond(result.statusCode, result)
    }
    authenticate("refresh") {
        get("/refreshToken") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal!!.payload.getClaim(claim).asString()
            val result = repository.refreshUserToken(email)
            call.respond(result.statusCode, result)
        }
    }
    authenticate("access") {
        get("/userInfo") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal!!.payload.getClaim(claim).asString()
            val result = repository.getUserInfo(email)
            call.respond(result.statusCode, result)
        }
    }
}