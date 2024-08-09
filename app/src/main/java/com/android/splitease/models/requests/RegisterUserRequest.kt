package com.android.splitease.models.requests

data class RegisterUserRequest(
    val userUuid: String,
    val name: String,
    val password: String,
    val email: String,
    val mobile: String
)
