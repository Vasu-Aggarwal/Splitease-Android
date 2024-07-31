package com.android.splitease.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.TransactionViewModel
import com.android.splitease.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailedGroupScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), 
                        userViewModel: UserViewModel = hiltViewModel(),
                        groupViewModel: GroupViewModel = hiltViewModel(),
                        navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val groupInfo: State<NetworkResult<AddGroupResponse>> = groupViewModel.groupInfo.collectAsState()

    val transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>> = transactionViewModel.transactions.collectAsState()
    val calculateDebt by transactionViewModel.calculateDebt.collectAsState()

    LaunchedEffect(groupId) {
        groupViewModel.getGroupInfo(groupId)
        transactionViewModel.getTransactionsByUser(groupId.toString())
        transactionViewModel.calculateDebt(groupId)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Match the height of the TopAppBar
            ) {
                // Background Image
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = groupInfo.value.data?.imageUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = groupInfo.value.data?.imageUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Foreground Image",
                    contentScale = ContentScale.Crop
                )
            }
            LargeTopAppBar(
                title = {
                    Text(
                        text = groupInfo.value.data?.name ?: "Group Details",
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White // Change text color for better contrast with background image
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent)
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    GroupTransactions(transactions, tokenManager, userViewModel, navController, groupInfo)
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupTransactions(
    transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>>,
    tokenManager: TokenManager,
    userViewModel: UserViewModel,
    navController: NavController,
    groupInfo: State<NetworkResult<AddGroupResponse>>
) {
    LazyColumn {
        item {
            GroupInfo(navController, groupInfo.value.data)
        }
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
                    val formattedDate = UtilMethods.formatDate(transaction.createdOn)
                    Text(text = formattedDate)
                    Column {
                        Text(text = transaction.description)
                        if (transaction.userUuid == tokenManager.getUserUuid().toString()) {
                            Text(text = "You paid ${UtilMethods.formatAmount(transaction.amount)}")
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
                            Text(text = "${user.value.data?.name} paid ${UtilMethods.formatAmount(transaction.amount)}")
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
    navController: NavController,
    data: AddGroupResponse?
) {
    val scrollState = rememberScrollState()
    data?.let {
//        Text(modifier = Modifier.padding(10.dp), text = data.name)
//        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(16.dp) // Optional padding for better layout
        ) {
            Button(onClick = { navController.navigate(Screen.AddUsersToGroupScreen.createRoute(data.groupId)) }) {
                Text(text = "Add Users")
            }
            Spacer(modifier = Modifier.width(8.dp)) // Optional spacing between buttons
            Button(onClick = { navController.navigate(Screen.UserDebtScreen.createRoute(data.groupId)) }) {
                Text(text = "Balances")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { navController.navigate(Screen.SettleUpPayerScreen.createRoute(data.groupId)) }) {
                Text(text = "Settle Up")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { navController.navigate(Screen.GroupSummaryScreen.createRoute(data.groupId)) }) {
                Text(text = "Totals")
            }
        }
    }
}

@Composable
fun SettleUpTransaction(){
    Text(text = "This is a settle up transaction")
}
