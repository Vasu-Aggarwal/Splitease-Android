package com.android.splitease.models.responses

data class SettleUpResponse(
    val transactionId: Int,
    val amount: Double,
    val groupId: Int,
    val payer: String,
    val receiver: String
)
