package com.android.splitease.models.requests

import com.android.splitease.utils.SplitBy

data class AddTransactionRequest(
    val amount: Double,
    val splitBy: SplitBy,
    val group: Int,
    val userUuid: String,
    val description: String,
    val category: String,
    val usersInvolved: Map<String, Double>
)
