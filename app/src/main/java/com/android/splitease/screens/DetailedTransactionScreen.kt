package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.navigation.Screen
import com.android.splitease.viewmodels.TransactionViewModel

@Composable
fun DetailedTransactionScreen(transactionId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), navController: NavController) {
    Column{
        Text(text = "This is $transactionId")
        Button(onClick = {
            transactionViewModel.deleteTransaction(transactionId)
            navController.popBackStack()
        }) {
            Text(text = "Delete Transaction")
        }
    }
}