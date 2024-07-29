package com.android.splitease.models.responses

data class GetGroupsByUserResponse(
    val groupId: Int,
    val name: String,
    val status: String,
    val imageUrl: String,
    val userBalance: Double
)
