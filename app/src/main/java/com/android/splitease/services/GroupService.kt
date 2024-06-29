package com.android.splitease.services

import com.android.splitease.models.responses.AddGroupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GroupService {
    @GET("/api/group/getGroupsByUser/{userUuid}")
    suspend fun getGroupsByUser(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<List<AddGroupResponse>>
}