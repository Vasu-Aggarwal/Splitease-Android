package com.android.splitease.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DetailedTransactionScreen(transactionId: Int) {
    Text(text = "This is $transactionId")
}