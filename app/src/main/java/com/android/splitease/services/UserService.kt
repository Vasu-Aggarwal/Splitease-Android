package com.android.splitease.services

import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetOverallUserBalance
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.models.responses.GetUserLogsResponse
import com.android.splitease.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("${AppConstants.USER_URL}/getUserByUuid/{userUuid}")
    suspend fun getUserByUuidAPi(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<GetUserByUuidResponse>

    @GET("${AppConstants.USER_URL}/isUserExists")
    suspend fun isUserExistsApi(@Header("Authorization") token: String, @Query("entityVal") userData: String): Response<List<GetUserByUuidResponse>>

    @GET("${AppConstants.USER_URL}/getUserActivities/{userUuid}")
    suspend fun getUserActivitiesApi(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<List<GetUserLogsResponse>>

    @GET("${AppConstants.USER_URL}/getOverallUserBalance/{userUuid}")
    suspend fun getOverallUserBalanceApi(
        @Header("Authorization") token: String,
        @Path("userUuid") userUuid: String,
        @Query("search_val") searchVal: String): Response<GetOverallUserBalance>
}