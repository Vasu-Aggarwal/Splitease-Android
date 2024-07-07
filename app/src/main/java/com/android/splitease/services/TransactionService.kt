package com.android.splitease.services

import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionService {
    @GET("/api/transaction/getTransactionsByGroup/{groupId}")
    suspend fun getTransactionsByGroupApi(@Header("Authorization") token: String, @Path("groupId") groupId: String): Response<List<GetTransactionsByGroupResponse>>

    @POST("/api/transaction/addTransaction")
    suspend fun addTransactionApi(@Header("Authorization") token: String, @Body addTransactionRequest: AddTransactionRequest): Response<AddTransactionResponse>
}