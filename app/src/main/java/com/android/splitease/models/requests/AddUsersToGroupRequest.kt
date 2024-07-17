package com.android.splitease.models.requests

data class AddUsersToGroupRequest(
    val groupId: Int,
    val userList: Set<String>
)
