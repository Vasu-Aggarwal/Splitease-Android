package com.android.splitease.services

import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.AppConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface GroupService {
    @GET("${AppConstants.GROUP_URL}/getGroupsByUser/{userUuid}")
    suspend fun getGroupsByUser(@Header("Authorization") token: String, @Path("userUuid") userUuid: String): Response<List<AddGroupResponse>>

    @GET("${AppConstants.GROUP_URL}/getGroupMembers/{groupId}")
    suspend fun getGroupMembersApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<List<CreateUserResponse>>

    @GET("${AppConstants.GROUP_URL}/getGroupMembers/{groupId}?v=2")
    suspend fun getGroupMembersV2Api(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<List<GetGroupMembersV2Response>>

    @Multipart
    @POST("${AppConstants.GROUP_URL}/addUpdateGroup")
    suspend fun addUpdateGroupApi(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("id") id: RequestBody?
        ): Response<AddGroupResponse>

    @POST("${AppConstants.GROUP_URL}/addUsersToGroup")
    suspend fun addUsersToGroupApi(@Header("Authorization") token: String, @Body addUsersToGroupRequest: AddUsersToGroupRequest): Response<AddUsersToGroupResponse>

}