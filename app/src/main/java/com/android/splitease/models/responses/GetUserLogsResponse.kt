package com.android.splitease.models.responses

data class GetUserLogsResponse(
    val id: Int,
    val userUuid: String,
    val activityType: String,
    val details: String,
    val createdOn: String
)
