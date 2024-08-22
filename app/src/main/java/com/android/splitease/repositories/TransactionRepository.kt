package com.android.splitease.repositories

import com.android.splitease.di.NetworkException
import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.requests.SettleUpRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetTransactionByIdResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.SettleUpResponse
import com.android.splitease.services.TransactionService
import com.android.splitease.utils.AppConstants
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

    private val _getTransaction = MutableStateFlow<NetworkResult<GetTransactionByIdResponse>>(NetworkResult.Idle())
    val getTransaction: StateFlow<NetworkResult<GetTransactionByIdResponse>>
        get() = _getTransaction

    private val _updateTransaction = MutableStateFlow<NetworkResult<AddTransactionResponse>>(NetworkResult.Idle())
    val updateTransaction: StateFlow<NetworkResult<AddTransactionResponse>>
        get() = _updateTransaction

    private val _restoreTransaction = MutableStateFlow<NetworkResult<GetTransactionByIdResponse>>(NetworkResult.Idle())
    val restoreTransaction: StateFlow<NetworkResult<GetTransactionByIdResponse>>
        get() = _restoreTransaction

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
        try {
            _transactions.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response =
                transactionService.getTransactionsByGroupApi("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _transactions.emit(NetworkResult.Success(response.body()!!))
            } else {
                _transactions.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _transactions.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _transactions.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun addTransaction(addTransactionRequest: AddTransactionRequest){
        try {
            _addTransaction.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response =
                transactionService.addTransactionApi("Bearer $authToken", addTransactionRequest)
            if (response.isSuccessful && response.body() != null) {
                _addTransaction.emit(NetworkResult.Success(response.body()!!))
            } else {
                _addTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _addTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _addTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun updateTransaction(addTransactionRequest: AddTransactionRequest){
        try {
            _updateTransaction.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response =
                transactionService.updateTransactionApi("Bearer $authToken", addTransactionRequest)
            if (response.isSuccessful && response.body() != null) {
                _updateTransaction.emit(NetworkResult.Success(response.body()!!))
            } else {
                _updateTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _updateTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _updateTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun deleteTransaction(transactionId: Int){
        try {
            _deleteTransaction.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response =
                transactionService.deleteTransactionApi("Bearer $authToken", transactionId)
            if (response.isSuccessful && response.body() != null) {
                _deleteTransaction.emit(NetworkResult.Success(response.body()!!))
            } else {
                _deleteTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _deleteTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _deleteTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun calculateDebt(groupId: Int){
        try {
            _calculateDebt.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = transactionService.calculateDebtApi("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _calculateDebt.emit(NetworkResult.Success(response.body()!!))
            } else {
                _calculateDebt.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _calculateDebt.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _calculateDebt.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun settleUp(settleUpRequest: SettleUpRequest){
        try {
            _settleUp.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = transactionService.settleUpApi("Bearer $authToken", settleUpRequest)
            if (response.isSuccessful && response.body() != null) {
                _settleUp.emit(NetworkResult.Success(response.body()!!))
            } else {
                _settleUp.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _settleUp.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _settleUp.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getTransactionById(transactionId: Int){
        try {
            _getTransaction.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = transactionService.getTransactionByIdApi("Bearer $authToken", transactionId)
            if (response.isSuccessful && response.body() != null) {
                _getTransaction.emit(NetworkResult.Success(response.body()!!))
            } else {
                _getTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _getTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _getTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun restoreTransaction(transactionId: Int){
        try {
            _restoreTransaction.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = transactionService.restoreTransactionApi("Bearer $authToken", transactionId)
            if (response.isSuccessful && response.body() != null) {
                _restoreTransaction.emit(NetworkResult.Success(response.body()!!))
            } else {
                _restoreTransaction.emit(NetworkResult.Error(response.errorBody()?.string()))
            }
        } catch (e: NetworkException){
            _restoreTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _restoreTransaction.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }
}