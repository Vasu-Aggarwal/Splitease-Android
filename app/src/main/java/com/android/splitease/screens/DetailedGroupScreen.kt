package com.android.splitease.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.TransactionViewModel

@Composable
fun DetailedGroupScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel()) {

    LaunchedEffect(groupId) {
        transactionViewModel.getGroupsByUser(groupId.toString())
    }

    val transactions: State<NetworkResult<List<AddTransactionResponse>>> = transactionViewModel.transactions.collectAsState()
    Column {
        GroupInfo()
        GroupTransactions(transactions)
    }
}

@Composable
fun GroupTransactions(transactions: State<NetworkResult<List<AddTransactionResponse>>>) {
    LazyColumn {
        transactions.value.data?.let { transactionList ->
            items(transactionList){transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: AddTransactionResponse) {
    Box{
        Column {
            Text(text = transaction.groupId.toString())
            Text(text = transaction.amount.toString())
            Text(text = transaction.category.categoryName)
            Text(text = transaction.createdOn)
        }
    }
}

@Composable
fun GroupInfo() {
    Text(text = "groupName")
}
