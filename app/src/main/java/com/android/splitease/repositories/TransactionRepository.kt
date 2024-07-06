package com.android.splitease.repositories

import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.services.TransactionService
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionService: TransactionService, private val tokenManager: TokenManager) {
    private val _transactions = MutableStateFlow<NetworkResult<List<GetTransactionsByGroupResponse>>>(NetworkResult.Idle())
    val transactions: StateFlow<NetworkResult<List<GetTransactionsByGroupResponse>>>
        get() = _transactions

    suspend fun transactionByGroupId(groupId: String){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.getTransactionsByGroupApi("Bearer $authToken", groupId)
        if (response.isSuccessful && response.body()!=null){
            _transactions.emit(NetworkResult.Success(response.body()!!))
        } else {
            _transactions.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }
}