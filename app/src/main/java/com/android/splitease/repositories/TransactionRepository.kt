package com.android.splitease.repositories

import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.requests.SettleUpRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.SettleUpResponse
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

    private val _addTransaction = MutableStateFlow<NetworkResult<AddTransactionResponse>>(NetworkResult.Idle())
    val addTransaction: StateFlow<NetworkResult<AddTransactionResponse>>
        get() = _addTransaction

    private val _updateTransaction = MutableStateFlow<NetworkResult<AddTransactionResponse>>(NetworkResult.Idle())
    val updateTransaction: StateFlow<NetworkResult<AddTransactionResponse>>
        get() = _updateTransaction

    private val _deleteTransaction = MutableStateFlow<NetworkResult<DeleteResponse>>(NetworkResult.Idle())
    val deleteTransaction: StateFlow<NetworkResult<DeleteResponse>>
        get() = _deleteTransaction

    private val _calculateDebt = MutableStateFlow<NetworkResult<CalculateDebtResponse>>(NetworkResult.Idle())
    val calculateDebt: StateFlow<NetworkResult<CalculateDebtResponse>>
        get() = _calculateDebt

    private val _settleUp = MutableStateFlow<NetworkResult<SettleUpResponse>>(NetworkResult.Idle())
    val settleUp: StateFlow<NetworkResult<SettleUpResponse>>
        get() = _settleUp

    suspend fun transactionByGroupId(groupId: String){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.getTransactionsByGroupApi("Bearer $authToken", groupId)
        if (response.isSuccessful && response.body()!=null){
            _transactions.emit(NetworkResult.Success(response.body()!!))
        } else {
            _transactions.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }

    suspend fun addTransaction(addTransactionRequest: AddTransactionRequest){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.addTransactionApi("Bearer $authToken", addTransactionRequest)
        if (response.isSuccessful && response.body()!=null){
            _addTransaction.emit(NetworkResult.Success(response.body()!!))
        } else {
            _addTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
        }
    }

    suspend fun updateTransaction(addTransactionRequest: AddTransactionRequest){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.updateTransactionApi("Bearer $authToken", addTransactionRequest)
        if (response.isSuccessful && response.body()!=null){
            _updateTransaction.emit(NetworkResult.Success(response.body()!!))
        } else {
            _updateTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
        }
    }

    suspend fun deleteTransaction(transactionId: Int){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.deleteTransactionApi("Bearer $authToken", transactionId)
        if (response.isSuccessful && response.body()!=null){
            _deleteTransaction.emit(NetworkResult.Success(response.body()!!))
        } else {
            _deleteTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
        }
    }

    suspend fun calculateDebt(groupId: Int){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.calculateDebtApi("Bearer $authToken", groupId)
        if (response.isSuccessful && response.body()!=null){
            _calculateDebt.emit(NetworkResult.Success(response.body()!!))
        } else {
            _calculateDebt.emit(NetworkResult.Error(response.errorBody()?.string()))
        }
    }

    suspend fun settleUp(settleUpRequest: SettleUpRequest){
        val authToken = tokenManager.getAuthToken()
        val response = transactionService.settleUpApi("Bearer $authToken", settleUpRequest)
        if (response.isSuccessful && response.body()!=null){
            _settleUp.emit(NetworkResult.Success(response.body()!!))
        } else {
            _settleUp.emit(NetworkResult.Error(response.errorBody()?.string()))
        }
    }
}