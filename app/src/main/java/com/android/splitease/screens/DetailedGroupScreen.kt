package com.android.splitease.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.TransactionViewModel
import com.android.splitease.viewmodels.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailedGroupScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), userViewModel: UserViewModel = hiltViewModel(), navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    LaunchedEffect(groupId) {
        transactionViewModel.getGroupsByUser(groupId.toString())
    }

    val transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>> = transactionViewModel.transactions.collectAsState()
    Column {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GroupInfo()
                GroupTransactions(transactions, tokenManager, userViewModel, navController)
            }
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddExpenseScreen.createRoute(groupId))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Add Expense")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupTransactions(
    transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>>,
    tokenManager: TokenManager,
    userViewModel: UserViewModel,
    navController: NavController
) {
    LazyColumn {
        Log.d("DGS", "GroupTransactions: Came here")
        transactions.value.data?.let { transactionList ->
            items(transactionList){transaction ->
                TransactionItem(transaction, tokenManager, userViewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(
    transaction: GetTransactionsByGroupResponse,
    tokenManager: TokenManager,
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    Card (
        onClick = {
            navController.navigate(Screen.DetailedTransactionScreen.createRoute(transaction.transactionId))
        }
    ){
        Box {
            Row {
                val utilMethods = UtilMethods()
                val formattedDate = utilMethods.formatDate(transaction.createdOn)
                Text(text = formattedDate)
                Column {
                    Text(text = transaction.description)
                    if (transaction.userUuid == tokenManager.getUserUuid().toString()) {
                        Text(text = "You paid Rs.${transaction.amount}")
                    } else {
                        LaunchedEffect(transaction.userUuid) {
                            userViewModel.getUserByUuid(transaction.userUuid)
                        }
                        val user: State<NetworkResult<CreateUserResponse>> =
                            userViewModel.user.collectAsState()
                        Text(text = "${user.value.data?.name} paid Rs. ${transaction.amount}")
                    }
                }
                Column {
                    if (transaction.loggedInUserTransaction == null) {
                        Text(text = " not involved")
                    } else {
                        if (transaction.loggedInUserTransaction.owedOrLent.equals("OWED")) {
                            Text(text = "you borrowed")
                        } else {
                            Text(text = "you lent")
                        }
                        Text(text = transaction.loggedInUserTransaction.amount.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun GroupInfo() {
    Text(text = "groupName")
}
