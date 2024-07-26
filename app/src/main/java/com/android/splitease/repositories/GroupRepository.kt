package com.android.splitease.repositories

import android.util.Log
import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.services.GroupService
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupService: GroupService, private val tokenManager: TokenManager) {
    private val _groups = MutableStateFlow<NetworkResult<List<AddGroupResponse>>>(NetworkResult.Idle())
    val groups: StateFlow<NetworkResult<List<AddGroupResponse>>>
        get() =_groups

    private val _groupMembers = MutableStateFlow<NetworkResult<List<CreateUserResponse>>>(NetworkResult.Idle())
    val groupMembers: StateFlow<NetworkResult<List<CreateUserResponse>>>
        get() = _groupMembers

    private val _groupMembersV2 = MutableStateFlow<NetworkResult<List<GetGroupMembersV2Response>>>(NetworkResult.Idle())
    val groupMembersV2: StateFlow<NetworkResult<List<GetGroupMembersV2Response>>>
        get() = _groupMembersV2

    private val _addUpdateGroup = MutableStateFlow<NetworkResult<AddGroupResponse>>(NetworkResult.Idle())
    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = _addUpdateGroup

    private val _addUsersToGroup = MutableStateFlow<NetworkResult<AddUsersToGroupResponse>>(NetworkResult.Idle())
    val addUsersToGroup: StateFlow<NetworkResult<AddUsersToGroupResponse>>
        get() = _addUsersToGroup

    suspend fun groupsByUser(){
        val authToken = tokenManager.getAuthToken()
        val userUuid = tokenManager.getUserUuid()!!
        val response = groupService.getGroupsByUser("Bearer $authToken", userUuid)
        if (response.isSuccessful && response.body()!=null){
            _groups.emit(NetworkResult.Success(response.body()!!))
        } else {
            _groups.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }

    suspend fun getGroupMembers(groupId: Int){
        val authToken = tokenManager.getAuthToken()
        val response = groupService.getGroupMembersApi("Bearer $authToken", groupId)
        if (response.isSuccessful && response.body()!=null){
            _groupMembers.emit(NetworkResult.Success(response.body()!!))
        } else {
            _groupMembers.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }

    suspend fun getGroupMembersV2(groupId: Int){
        val authToken = tokenManager.getAuthToken()
        val response = groupService.getGroupMembersV2Api("Bearer $authToken", groupId)
        if (response.isSuccessful && response.body()!=null){
            _groupMembersV2.emit(NetworkResult.Success(response.body()!!))
        } else {
            _groupMembersV2.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }

    suspend fun addUpdateGroup(name: String, id: Int, image: File){
        try {// Create request body for text data
            val nameRequestBody = RequestBody.create(MultipartBody.FORM, name)
            val idRequestBody = RequestBody.create(MultipartBody.FORM, id.toString())
            // Create multipart body for image
            Log.d("img address", "addUpdateGroup: ${image.canonicalPath}")
            val imagePart = image.let {
                val imageRequestBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, imageRequestBody)
            }

            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.addUpdateGroupApi(
                "Bearer $authToken",
                imagePart,
                nameRequestBody,
                idRequestBody
            )
            if (response.isSuccessful && response.body() != null) {
                _addUpdateGroup.emit(NetworkResult.Success(response.body()!!))
            } else {
                _addUpdateGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: Exception){
            Log.e("addUpdateGroup", "addUpdateGroup: ", e.cause)
        }
    }

    suspend fun addUsersToGroup(addUsersToGroupRequest: AddUsersToGroupRequest){
        val authToken = tokenManager.getAuthToken()!!
        val response = groupService.addUsersToGroupApi("Bearer $authToken", addUsersToGroupRequest)
        if (response.isSuccessful && response.body()!=null){
            _addUsersToGroup.emit(NetworkResult.Success(response.body()!!))
        } else {
            _addUsersToGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }
}