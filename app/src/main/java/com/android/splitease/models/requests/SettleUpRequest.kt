package com.android.splitease.models.requests

data class SettleUpRequest(
    val payer: String,
    val receiver: String,
    val amount: Double,
    val group: Int
)
