package com.android.splitease.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.TransactionViewModel
import com.android.splitease.viewmodels.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailedGroupScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), 
                        userViewModel: UserViewModel = hiltViewModel(), 
                        navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    LaunchedEffect(groupId) {
        transactionViewModel.getTransactionsByUser(groupId.toString())
        transactionViewModel.calculateDebt(groupId)
    }

    val transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>> = transactionViewModel.transactions.collectAsState()
    val calculateDebt by transactionViewModel.calculateDebt.collectAsState()
    Column {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GroupInfo(groupId, navController)
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
    Card (
        onClick = {
            navController.navigate(Screen.DetailedTransactionScreen.createRoute(transaction.transactionId))
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ){
        var userState by remember { mutableStateOf<GetUserByUuidResponse?>(null) }
        Box {
            if (transaction.description == null){
                SettleUpTransaction()
            } else {
                Row {
                    val utilMethods = UtilMethods()
                    val formattedDate = utilMethods.formatDate(transaction.createdOn)
                    Text(text = formattedDate)
                    Column {
                        Text(text = transaction.description)
                        if (transaction.userUuid == tokenManager.getUserUuid().toString()) {
                            Text(text = "You paid Rs.${transaction.amount}")
                        } else {
                            if(userState == null) {
                                LaunchedEffect(transaction.userUuid) {
                                    userViewModel.getUserByUuid(transaction.userUuid)
                                }
                            }
                            val user: State<NetworkResult<GetUserByUuidResponse>> =
                                userViewModel.user.collectAsState()
                            val userData = userViewModel.user.collectAsState().value
                            if (userData is NetworkResult.Success){
                                userState = userData.data
                            }
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
}

@Composable
fun GroupInfo(
    groupId: Int,
    navController: NavController
) {
    Row{
        Button(onClick = { navController.navigate(Screen.AddUsersToGroupScreen.createRoute(groupId)) }) {
            Text(text = "Add Users")
        }
        Button(onClick = { navController.navigate(Screen.UserDebtScreen.createRoute(groupId)) }) {
            Text(text = "Balances")
        }
        Button(onClick = { navController.navigate(Screen.SettleUpPayerScreen.createRoute(groupId)) }) {
            Text(text = "Settle Up")
        }
    }
}

@Composable
fun SettleUpTransaction(){
    Text(text = "This is a settle up transaction")
}
