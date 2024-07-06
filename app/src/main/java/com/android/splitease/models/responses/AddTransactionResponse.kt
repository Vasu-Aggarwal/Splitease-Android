package com.android.splitease.models.responses

import java.time.LocalDateTime

data class AddTransactionResponse(
    val transactionId: Int,
    val amount: Double,
    val splitBy: String,
    val groupId: Int,
    val userUuid: String,
    val category: AddCategoryResponse,
    val createdOn: String
)