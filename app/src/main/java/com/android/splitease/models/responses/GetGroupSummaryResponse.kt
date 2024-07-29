package com.android.splitease.models.responses

data class GetGroupSummaryResponse(
    val groupId: Int,
    val groupName: String,
    val totalGroupSpending: Double,
    val userPaidFor: Double,
    val userTotalShare: Double
)
