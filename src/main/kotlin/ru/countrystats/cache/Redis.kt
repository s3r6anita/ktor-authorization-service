package ru.countrystats.cache

import io.ktor.server.config.*
import redis.clients.jedis.Jedis
import ru.countrystats.security.hashPassword

private const val STORE_TOKEN_TIME = 3600L

class RedisTokenStore(
    config: ApplicationConfig,
    private val jedis: Jedis = Jedis(config.property("ktor.redis.url").getString())
) {
    // Добавление пары email: token
    fun addToken(email: String, token: String) {
        jedis.setex(email, STORE_TOKEN_TIME, hashPassword(token))
    }

    // Поиск токена по email
    fun getToken(email: String): String? {
        return jedis.get(email)
    }

    // Удаление токена по email
    // возвращает количество удаленных ключей
    fun removeToken(email: String) = jedis.del(email)
}