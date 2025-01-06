package ru.countrystats.cache

import redis.clients.jedis.Jedis

private const val STORE_TOKEN_TIME = 3600L

class RedisTokenStore(private val jedis: Jedis = Jedis("localhost")) {

    // Добавление пары email: token
    fun addToken(email: String, token: String) {
        jedis.setex(email, STORE_TOKEN_TIME, token)
    }

    // Поиск токена по email
    fun getToken(email: String): String? {
        return jedis.get(email)
    }

    // Удаление токена по email
    // возвращает количество удаленных ключей
    fun removeToken(email: String) = jedis.del(email)
}