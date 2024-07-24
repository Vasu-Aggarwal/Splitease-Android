package com.android.splitease.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val groupRepository: GroupRepository) : ViewModel() {
    val groups: StateFlow<NetworkResult<List<AddGroupResponse>>>
        get() = groupRepository.groups

    val groupMembers: StateFlow<NetworkResult<List<CreateUserResponse>>>
        get() = groupRepository.groupMembers

    val groupMembersV2: StateFlow<NetworkResult<List<GetGroupMembersV2Response>>>
        get() = groupRepository.groupMembersV2

    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = groupRepository.addUpdateGroup

    val addUsersToGroup: StateFlow<NetworkResult<AddUsersToGroupResponse>>
        get() = groupRepository.addUsersToGroup

    init {
        viewModelScope.launch {
            groupRepository.groupsByUser()
        }
    }

    fun getGroupsByUser() {
        viewModelScope.launch {
            groupRepository.groupsByUser()
        }
    }

    fun getGroupMembers(groupId: Int) {
        viewModelScope.launch {
            groupRepository.getGroupMembers(groupId)
        }
    }

    fun getGroupMembersV2(groupId: Int) {
        viewModelScope.launch {
            groupRepository.getGroupMembersV2(groupId)
        }
    }

    fun addUpdateGroup(addGroupRequest: AddGroupRequest){
        viewModelScope.launch {
            groupRepository.addUpdateGroup(addGroupRequest)
        }
    }

    fun addUsersToGroup(addUsersToGroupRequest: AddUsersToGroupRequest){
        viewModelScope.launch {
            groupRepository.addUsersToGroup(addUsersToGroupRequest)
        }
    }
}