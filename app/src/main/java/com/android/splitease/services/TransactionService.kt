package com.android.splitease.services

import com.android.splitease.models.responses.AddTransactionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TransactionService {
    @GET("/api/transaction/getTransactionsByGroup/{groupId}")
    suspend fun getTransactionsByGroupApi(@Header("Authorization") token: String, @Path("groupId") groupId: String): Response<List<AddTransactionResponse>>
}