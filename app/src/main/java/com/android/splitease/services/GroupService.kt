package com.android.splitease.services

import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GroupService {
    @GET("${AppConstants.GROUP_URL}/getGroupsByUser/{userUuid}")
    suspend fun getGroupsByUser(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<List<AddGroupResponse>>

    @GET("${AppConstants.GROUP_URL}/getGroupMembers/{groupId}")
    suspend fun getGroupMembersApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<Set<CreateUserResponse>>

    @GET("${AppConstants.GROUP_URL}/getGroupMembers/{groupId}?v=2")
    suspend fun getGroupMembersV2Api(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<Set<GetGroupMembersV2Response>>
}