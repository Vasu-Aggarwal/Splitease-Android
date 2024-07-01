package com.android.splitease.models.responses

data class UserLoginResponse(
    val token: String,
    val refreshToken: String,
    val userUuid: String
)
