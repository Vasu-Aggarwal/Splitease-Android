package com.android.splitease.services

import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.utils.AppConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface GroupService {
    @GET("${AppConstants.GROUP_URL}/getGroupsByUser/{userUuid}")
    suspend fun getGroupsByUser(@Header("Authorization") token: String, @Path("userUuid") userUuid: String, @Query("search_by") searchBy: String): Response<List<GetGroupsByUserResponse>>

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

    @GET("${AppConstants.GROUP_URL}/getGroupSpendingSummary/{groupId}")
    suspend fun getGroupSpendingSummaryApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<GetGroupSummaryResponse>

    @GET("${AppConstants.GROUP_URL}/getGroupInfo/{groupId}")
    suspend fun getGroupInfoApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<AddGroupResponse>

    @GET("${AppConstants.GROUP_URL}/export/{groupId}")
    @Streaming
    suspend fun downloadExcel(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<ResponseBody>

}