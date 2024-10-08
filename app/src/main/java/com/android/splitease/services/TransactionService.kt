package com.android.splitease.services

import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.requests.SettleUpRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetTransactionByIdResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.SettleUpResponse
import com.android.splitease.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionService {
    @GET("${AppConstants.TRANSACTION_URL}/getTransactionsByGroup/{groupId}")
    suspend fun getTransactionsByGroupApi(@Header("Authorization") token: String, @Path("groupId") groupId: String): Response<List<GetTransactionsByGroupResponse>>

    @GET("${AppConstants.TRANSACTION_URL}/getTransactionById/{transactionId}")
    suspend fun getTransactionByIdApi(@Header("Authorization") token: String, @Path("transactionId") transactionId: Int): Response<GetTransactionByIdResponse>

    @POST("${AppConstants.TRANSACTION_URL}/addTransaction")
    suspend fun addTransactionApi(@Header("Authorization") token: String, @Body addTransactionRequest: AddTransactionRequest): Response<AddTransactionResponse>

    @POST("${AppConstants.TRANSACTION_URL}/settleUp")
    suspend fun settleUpApi(@Header("Authorization") token: String, @Body settleUpRequest: SettleUpRequest): Response<SettleUpResponse>

    @POST("${AppConstants.TRANSACTION_URL}/updateTransaction")
    suspend fun updateTransactionApi(@Header("Authorization") token: String, @Body addTransactionRequest: AddTransactionRequest): Response<AddTransactionResponse>

    @POST("${AppConstants.TRANSACTION_URL}/restoreTransaction/{transactionId}")
    suspend fun restoreTransactionApi(@Header("Authorization") token: String, @Path("transactionId") transactionId: Int): Response<GetTransactionByIdResponse>

    @GET("${AppConstants.TRANSACTION_URL}/calculateDebt/{groupId}")
    suspend fun calculateDebtApi(@Header("Authorization") token: String, @Path("groupId") groupId: Int): Response<CalculateDebtResponse>

    @DELETE("${AppConstants.TRANSACTION_URL}/deleteTransaction/{transactionId}")
    suspend fun deleteTransactionApi(@Header("Authorization") token: String, @Path("transactionId") transactionId: Int): Response<DeleteResponse>
}