package com.android.splitease.services

import com.android.splitease.models.responses.CreateUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserService {
    @GET("/api/user/getUserByUuid/{userUuid}")
    suspend fun getUserByUuidAPi(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<CreateUserResponse>
}