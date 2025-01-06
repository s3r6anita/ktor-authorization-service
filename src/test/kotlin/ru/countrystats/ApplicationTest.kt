package ru.countrystats

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRedirectHttps() = testApplication {
        application {
            module()
        }
        val response = client.get("/") {
            url { protocol = URLProtocol.HTTPS }
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

}
