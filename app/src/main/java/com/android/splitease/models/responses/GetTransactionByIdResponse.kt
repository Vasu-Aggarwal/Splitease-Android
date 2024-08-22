package com.android.splitease.models.responses

data class GetTransactionByIdResponse(
    val description: String,
    val transactionId: Int,
    val amount: Double,
    val splitBy: String,
    val groupId: Int,
    val status: Int,
    val userUuid: String,
    val payerName: String,
    val category: AddCategoryResponse,
    val userLedgerDetails: List<UserLedgerDetails>,
    val createdOn: String
)

data class UserLedgerDetails(
    val userUuid: String,
    val name: String,
    val amount: Double,
    val owedOrLent: String
)