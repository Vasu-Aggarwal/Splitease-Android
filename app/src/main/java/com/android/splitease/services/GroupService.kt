package com.android.splitease.services

import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GroupService {
    @GET("/api/group/getGroupsByUser/{userUuid}")
    suspend fun getGroupsByUser(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<List<AddGroupResponse>>

    @GET("/api/group/getGroupMembers/{groupId}")
    suspend fun getGroupMembersApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<Set<CreateUserResponse>>

    @GET("/api/group/getGroupMembers/{groupId}?v=2")
    suspend fun getGroupMembersV2Api(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<Set<GetGroupMembersV2Response>>
}