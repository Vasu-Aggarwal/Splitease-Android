package com.android.splitease.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.requests.SettleUpRequest
import com.android.splitease.navigation.Screen
import com.android.splitease.viewmodels.TransactionViewModel

@Composable
fun SettleUpScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    groupId: Int,
    payerUuid: String,
    receiverUuid: String
) {
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Handle amount input change
    fun handleAmountChange(value: String) {
        amount = value
    }

    // Handle submit
    fun handleSubmit() {
        val amountValue = amount.toDoubleOrNull()
        if (amountValue != null && amountValue > 0) {
            val settleUpRequest = SettleUpRequest(payerUuid, receiverUuid, amountValue, groupId)

            // Call API to submit the amount
            transactionViewModel.settleUp(settleUpRequest)

            // Show success message or navigate as needed
            message = "Transaction submitted successfully"
            navController.navigate(Screen.DetailedGroupScreen.createRoute(groupId)) // or navigate to another screen if needed
        } else {
            message = "Please enter a valid amount"
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Amount Paid",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = amount,
            onValueChange = { handleAmountChange(it) },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { handleSubmit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(text = message)
        }
    }
}