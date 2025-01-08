package ru.countrystats.database.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val data: AuthData
)

data class AuthData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)