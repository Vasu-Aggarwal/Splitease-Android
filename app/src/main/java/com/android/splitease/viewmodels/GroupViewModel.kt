package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val groupRepository: GroupRepository) : ViewModel() {
    val groups: StateFlow<NetworkResult<List<GetGroupsByUserResponse>>>
        get() = groupRepository.groups

    val groupMembers: StateFlow<NetworkResult<List<CreateUserResponse>>>
        get() = groupRepository.groupMembers

    val groupMembersV2: StateFlow<NetworkResult<List<GetGroupMembersV2Response>>>
        get() = groupRepository.groupMembersV2

    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = groupRepository.addUpdateGroup

    val addUsersToGroup: StateFlow<NetworkResult<AddUsersToGroupResponse>>
        get() = groupRepository.addUsersToGroup

    val groupSummary: StateFlow<NetworkResult<GetGroupSummaryResponse>>
        get() = groupRepository.groupSummary

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

    fun addUpdateGroup(name: String, id: Int?, image: File){
        viewModelScope.launch {
            groupRepository.addUpdateGroup(name, id, image)
        }
    }

    fun addUsersToGroup(addUsersToGroupRequest: AddUsersToGroupRequest){
        viewModelScope.launch {
            groupRepository.addUsersToGroup(addUsersToGroupRequest)
        }
    }

    fun getGroupSummary(groupId: Int){
        viewModelScope.launch {
            groupRepository.getGroupSummary(groupId)
        }
    }
}