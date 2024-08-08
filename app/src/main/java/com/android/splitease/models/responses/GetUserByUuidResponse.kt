package com.android.splitease.models.responses

data class GetUserByUuidResponse(
    val name: String,
    val userUuid: String,
    val email: String,
    val mobile: String
)
