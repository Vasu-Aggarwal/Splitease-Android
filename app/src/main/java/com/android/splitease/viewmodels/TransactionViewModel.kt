package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.requests.SettleUpRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetTransactionByIdResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.repositories.TransactionRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepository): ViewModel() {
    val transactions: StateFlow<NetworkResult<List<GetTransactionsByGroupResponse>>>
        get() = transactionRepository.transactions

    val addTransaction: StateFlow<NetworkResult<AddTransactionResponse>>
        get() = transactionRepository.addTransaction

    val deleteTransaction: StateFlow<NetworkResult<DeleteResponse>>
        get() = transactionRepository.deleteTransaction

    val calculateDebt: StateFlow<NetworkResult<CalculateDebtResponse>>
        get() = transactionRepository.calculateDebt

    val getTransaction: StateFlow<NetworkResult<GetTransactionByIdResponse>>
        get() = transactionRepository.getTransaction

    val restoreTransaction: StateFlow<NetworkResult<GetTransactionByIdResponse>>
        get() = transactionRepository.restoreTransaction

    fun getTransactionsByUser(groupId: String) {
        viewModelScope.launch {
            transactionRepository.transactionByGroupId(groupId)
        }
    }

    fun addTransaction(transaction: AddTransactionRequest) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(transactionId: Int){
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
        }
    }

    fun calculateDebt(groupId: Int){
        viewModelScope.launch {
            transactionRepository.calculateDebt(groupId)
        }
    }

    fun settleUp(settleUpRequest: SettleUpRequest){
        viewModelScope.launch {
            transactionRepository.settleUp(settleUpRequest)
        }
    }

    fun getTransactionById(transactionId: Int){
        viewModelScope.launch {
            transactionRepository.getTransactionById(transactionId)
        }
    }

    fun restoreTransaction(transactionId: Int){
        viewModelScope.launch {
            transactionRepository.restoreTransaction(transactionId)
        }
    }
}