package com.android.splitease.repositories

import com.android.splitease.models.responses.AddGroupResponse
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
}