package ru.countrystats.repository

import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.countrystats.database.UserService
import ru.countrystats.database.model.LoginUserParams
import ru.countrystats.database.model.RegisterUserParams
import ru.countrystats.util.BaseResponse
import ru.countrystats.util.checkPassword
import ru.countrystats.util.isValidEmail

interface IUserRepository {
    suspend fun registerUser(params: RegisterUserParams): BaseResponse<Any>
    suspend fun loginUser(params: LoginUserParams): BaseResponse<Any>
    suspend fun refreshUserToken(params: TokenPair): BaseResponse<Any>
}


class UserRepository(
    private val userService: UserService = UserService()
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
                    userService.create(params)
                    BaseResponse.SuccessResponse()
//                    логика создания и возврата токена
//                    val token = JwtConfig.instance.generateToken(user.email)
//                    InMemoryCache.tokens.add(TokenPair(user.email, token))
//                    BaseResponse.SuccessResponse(data = hashMapOf("token" to token))
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
                    BaseResponse.SuccessResponse()
//                    логика создания и возврата токена
//                    val token = JwtConfig.instance.generateToken(user.email)
//                    InMemoryCache.tokens.add(TokenPair(user.email, token))
//                    BaseResponse.SuccessResponse(data = hashMapOf("token" to token))
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

    override suspend fun refreshUserToken(params: TokenPair): BaseResponse<Any> {
        TODO("Not yet implemented")
    }

//    override suspend fun refreshTokenUser(params: TokenPair): BaseResponse<Any> {
//        val tokenPair = InMemoryCache.tokens.firstOrNull{ it == params }
//        return if (tokenPair != null) {
//            InMemoryCache.tokens.remove(tokenPair)
//            val newToken = JwtConfig.instance.generateToken(params.email)
//            InMemoryCache.tokens.add(TokenPair(params.email, newToken))
//            BaseResponse.SuccessResponse(data = params, hash = hashMapOf("token" to newToken))
//        } else {
//            BaseResponse.ErrorResponse(msg = "Invalid email or token")
//        }
//    }

    private suspend fun isEmailExist(email: String): Boolean = userService.userByEmail(email) != null
}