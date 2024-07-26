package com.android.splitease.models.responses

data class AddGroupResponse(
    val groupId: Int,
    val name: String,
    val totalAmount: Double,
    val status: String,
    val imageUrl: String
)
