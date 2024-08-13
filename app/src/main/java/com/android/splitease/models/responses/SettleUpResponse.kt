package com.android.splitease.models.responses

import java.time.LocalDateTime

data class SettleUpResponse(
    val transactionId: Int,
    val amount: Double,
    val groupId: Int,
    val payer: String,
    val receiver: String,
    val payerName: String,
    val receiverName: String,
    val createdOn: LocalDateTime
)
