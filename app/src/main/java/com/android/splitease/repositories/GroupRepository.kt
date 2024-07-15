package com.android.splitease.repositories

import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.services.GroupService
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupService: GroupService, private val tokenManager: TokenManager) {
    private val _groups = MutableStateFlow<NetworkResult<List<AddGroupResponse>>>(NetworkResult.Idle())
    val groups: StateFlow<NetworkResult<List<AddGroupResponse>>>
        get() =_groups

    private val _groupMembers = MutableStateFlow<NetworkResult<Set<CreateUserResponse>>>(NetworkResult.Idle())
    val groupMembers: StateFlow<NetworkResult<Set<CreateUserResponse>>>
        get() = _groupMembers

    private val _groupMembersV2 = MutableStateFlow<NetworkResult<Set<GetGroupMembersV2Response>>>(NetworkResult.Idle())
    val groupMembersV2: StateFlow<NetworkResult<Set<GetGroupMembersV2Response>>>
        get() = _groupMembersV2

    private val _addUpdateGroup = MutableStateFlow<NetworkResult<AddGroupResponse>>(NetworkResult.Idle())
    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = _addUpdateGroup

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

    suspend fun addUpdateGroup(addGroupRequest: AddGroupRequest){
        val authToken = tokenManager.getAuthToken()!!
        val response = groupService.addUpdateGroupApi("Bearer $authToken", addGroupRequest)
        if (response.isSuccessful && response.body()!=null){
            _addUpdateGroup.emit(NetworkResult.Success(response.body()!!))
        } else {
            _addUpdateGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }
}