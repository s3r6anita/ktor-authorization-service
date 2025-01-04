package ru.countrystats.database.model

import java.time.LocalDateTime

data class User(
    val id: Long,
    val email: String,
    val password: String,
    val name: String,
    val createdAt: LocalDateTime,
    val authToken: String? = null,
)