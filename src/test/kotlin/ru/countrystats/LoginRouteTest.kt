package ru.countrystats

import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import ru.countrystats.database.UserService
import ru.countrystats.database.model.AuthResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginRouteTest {
    @Test
    fun testRedirectHttps() = testApplication {
        application {
            module()
        }
        val gson = Gson()

        // регистрируем пользователя
        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "test@test.com", "password": "password", "name": "Name"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // входим зарегистрированным пользователем
        val loginResponse = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "test@test.com", "password": "password"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        // получаем accessToken пользователя после входа
        val responseBody = loginResponse.bodyAsText()
        val authResponse = gson.fromJson(responseBody, AuthResponse::class.java)
        val accessToken = authResponse.data.accessToken

        // выполняем авторизованный запрос userInfo
        val userInfoResponse = client.get("/userInfo") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        assertEquals(HttpStatusCode.OK, userInfoResponse.status)

        // удаляем добавленного пользователя
        val service = UserService()
        service.delete("test@test.com")
    }
}


