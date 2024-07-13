package com.android.splitease.models.responses

data class RefreshTokenResponse(
    val token: String,
    val userUuid: String,
    val refreshToken: String
)
