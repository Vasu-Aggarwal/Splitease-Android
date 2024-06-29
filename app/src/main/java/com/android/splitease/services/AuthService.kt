package com.android.splitease.services

import com.android.splitease.models.requests.UserLoginRequest
import com.android.splitease.models.responses.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/login")
    suspend fun loginUser(@Body user: UserLoginRequest): Response<UserLoginResponse>
}