package com.android.splitease.services

import com.android.splitease.models.requests.UserLoginRequest
import com.android.splitease.models.responses.RefreshTokenResponse
import com.android.splitease.models.responses.UserLoginResponse
import com.android.splitease.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("${AppConstants.AUTH_URL}/login")
    suspend fun loginUser(@Body user: UserLoginRequest): Response<UserLoginResponse>

    @POST("${AppConstants.AUTH_URL}/refreshToken")
    suspend fun refreshToken(@Body refreshToken: String): Response<RefreshTokenResponse>
}