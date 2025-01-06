package ru.countrystats.repository

import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.countrystats.cache.RedisTokenStore
import ru.countrystats.database.UserService
import ru.countrystats.database.model.LoginUserParams
import ru.countrystats.database.model.RegisterUserParams
import ru.countrystats.security.JwtConfig
import ru.countrystats.util.BaseResponse
import ru.countrystats.security.checkPassword
import ru.countrystats.util.isValidEmail

interface IUserRepository {
    suspend fun registerUser(params: RegisterUserParams): BaseResponse<Any>
    suspend fun loginUser(params: LoginUserParams): BaseResponse<Any>
    suspend fun refreshUserToken(refreshToken: String): BaseResponse<Any>
}


class UserRepository(
    private val userService: UserService = UserService(),
    private val tokenStore: RedisTokenStore = RedisTokenStore(),
) : IUserRepository {

    override suspend fun registerUser(params: RegisterUserParams): BaseResponse<Any> {
        return if (params.email.isValidEmail()) {
            if (isEmailExist(params.email)) {
                BaseResponse.ErrorResponse(
                    errorStatusCode = HttpStatusCode.Conflict,
                    msg = "User with such email is already exist"
                )
            } else {
                try {
                    val refreshToken = JwtConfig.instance.generateRefreshToken(params.email)
                    userService.create(params, refreshToken)

                    val token = JwtConfig.instance.generateAccessToken(params.email)
                    tokenStore.addToken(params.email, token)
                    BaseResponse.SuccessResponse(
                        data = hashMapOf(
                            "access_token" to token,
                            "refresh_token" to refreshToken
                        )
                    )
                } catch (e: ExposedSQLException) {
                    BaseResponse.ErrorResponse(msg = "You are already registered")
                }
            }
        } else {
            BaseResponse.ErrorResponse(msg = "Invalid email")
        }
    }

    override suspend fun loginUser(params: LoginUserParams): BaseResponse<Any> {
        return if (isEmailExist(params.email)) {
            val user = userService.userByEmail(params.email)
            if (user != null) {
                if (checkPassword(params.password, user.password)) {
                    val refreshToken = JwtConfig.instance.generateRefreshToken(params.email)
                    userService.updateRefreshToken(params.email, refreshToken)

                    val token = JwtConfig.instance.generateAccessToken(user.email)
                    tokenStore.addToken(params.email, token)
                    BaseResponse.SuccessResponse(
                        data = hashMapOf(
                            "access_token" to token,
                            "refresh_token" to refreshToken
                        )
                    )
                } else {
                    BaseResponse.ErrorResponse(msg = "Invalid password")
                }
            } else {
                BaseResponse.ErrorResponse(msg = "Invalid email")
            }
        } else {
            BaseResponse.ErrorResponse(msg = "User with this email does not exist")
        }
    }

    override suspend fun refreshUserToken(email: String): BaseResponse<Any> {
        val user = userService.userByEmail(email)
        return if (user != null) {
            tokenStore.removeToken(email)
            val newToken = JwtConfig.instance.generateAccessToken(email)
            tokenStore.addToken(email, newToken)
            BaseResponse.SuccessResponse(data = hashMapOf("access_token" to newToken))
        } else {
            BaseResponse.ErrorResponse(msg = "No user with this email or refresh token")
        }
    }

    private suspend fun isEmailExist(email: String): Boolean = userService.userByEmail(email) != null
}