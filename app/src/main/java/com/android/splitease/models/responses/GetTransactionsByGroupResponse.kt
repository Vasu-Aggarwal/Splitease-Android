package com.android.splitease.models.responses

import java.time.LocalDateTime

data class GetTransactionsByGroupResponse(
    val description: String,
    val transactionId: Int,
    val amount: Double,
    val splitBy: String,
    val groupId: Int,
    val userUuid: String,
    val category: AddCategoryResponse,
    val createdOn: String,
    val loggedInUserTransaction: LoggedInUserTransaction
)