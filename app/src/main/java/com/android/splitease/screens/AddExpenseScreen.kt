package com.android.splitease.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.SplitBy
import com.android.splitease.utils.TokenManager
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.TransactionViewModel
import com.google.gson.Gson

@Composable
fun AddExpenseScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), groupViewModel: GroupViewModel = hiltViewModel(), navController: NavController){
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val userUuid = tokenManager.getUserUuid()
    val addTransaction: State<NetworkResult<AddTransactionResponse>> = transactionViewModel.addTransaction.collectAsState()
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableDoubleStateOf(0.00) }
    var message by remember { mutableStateOf("") }
    val groupMembers by groupViewModel.groupMembersV2.collectAsState()

    // Retrieve the selected user's name and UUID from the savedStateHandle
    val selectedUserName = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selectedUserName", " ")?.collectAsState()
    val selectedUserUuid = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selectedUserUuid", " ")?.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Enter a description") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = if (amount == 0.0) "" else amount.toString(),
            onValueChange = { it.toDoubleOrNull()?.let { amt -> amount = amt } },
            label = { Text("Amount") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (description.isNotBlank() && amount>0.00) {
                    groupViewModel.getGroupMembersV2(groupId)
                } else {
                    message = "Please enter a valid description and amount"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(text = message)
        }
        when (val result = addTransaction.value) {
            is NetworkResult.Success -> {
                message = "Transaction added successfully"
                navController.popBackStack(Screen.DetailedGroupScreen.createRoute(groupId), false)
            }
            is NetworkResult.Error -> {
                message = result.message ?: "An error occurred"
            }
            is NetworkResult.Loading -> {
                message = "Adding transaction..."
            }

            is NetworkResult.Idle -> message = "Idle"
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Paid by", modifier = Modifier.padding(end = 8.dp))
                Button(
                    modifier = Modifier.padding(8.dp)
                        .shadow(5.dp),
                    shape = RoundedCornerShape(15),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    onClick = { navController.navigate(Screen.SelectPayingUserScreen.createRoute(groupId)) }
                ) {
                    Text(text = selectedUserName?.value ?: "You", maxLines = 1)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "and split", modifier = Modifier.padding(end = 8.dp))
                Button(
                    modifier = Modifier.padding(8.dp)
                        .shadow(5.dp),
                    shape = RoundedCornerShape(15),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    onClick = { navController.navigate(Screen.SplitMethodScreen.createRoute(groupId, amount)) }
                ) {
                    Text(text = "Equally", maxLines = 1)
                }
            }
        }

    }
    
    LaunchedEffect(groupMembers) {
        if (groupMembers is NetworkResult.Success){
            val members = (groupMembers as NetworkResult.Success).data
            if (!members.isNullOrEmpty()) {
                val equalAmount = amount / members.size
                val contributions = mutableMapOf<String, Double>()

                // Distribute the amount equally
                members.forEachIndexed { index, member ->
                    contributions[member.email] = if (index == members.size - 1) {
                        // Adjust the last member's contribution to ensure the total is correct
                        amount - contributions.values.sum()
                    } else {
                        equalAmount
                    }
                }

                val addTransactionRequest = AddTransactionRequest(
                    amount = amount,
                    splitBy = SplitBy.EQUAL,
                    group = groupId,
                    userUuid = userUuid!!,
                    description = description,
                    category = "Adventure",
                    usersInvolved = contributions!!
                )
                val gson = Gson()
                val json = gson.toJson(addTransactionRequest)
                Log.d("AES", "AddExpenseScreen: $json")
                transactionViewModel.addTransaction(addTransactionRequest)
            }
        }
    }
    
}