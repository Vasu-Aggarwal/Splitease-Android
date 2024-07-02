package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.repositories.TransactionRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepository): ViewModel() {
    val transactions: StateFlow<NetworkResult<List<AddTransactionResponse>>>
        get() = transactionRepository.transactions

    fun getGroupsByUser(groupId: String) {
        viewModelScope.launch {
            transactionRepository.transactionByGroupId(groupId)
        }
    }
}