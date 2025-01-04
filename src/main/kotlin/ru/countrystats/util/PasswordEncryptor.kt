package ru.countrystats.util

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory

val argon2: Argon2 = Argon2Factory.create()

fun hashPassword(password: String): String {
    return argon2.hash(10, 65536, 1, password.toCharArray()) // Параметры: итерации, память, параллелизм
}

fun checkPassword(password: String, hashed: String): Boolean {
    return argon2.verify(hashed, password.toCharArray())
}