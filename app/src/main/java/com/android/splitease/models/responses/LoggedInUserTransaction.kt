package com.android.splitease.models.responses

data class LoggedInUserTransaction(
    val userUuid: String,
    val amount: Double,
    val owedOrLent: String
)
