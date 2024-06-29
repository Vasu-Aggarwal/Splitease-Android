package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val groupRepository: GroupRepository) : ViewModel() {
    val groups: StateFlow<NetworkResult<List<AddGroupResponse>>>
        get() = groupRepository.groups

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
}