package com.android.splitease.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.splitease.R
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Grey400
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.TransactionViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

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

    val scrollState = rememberLazyListState()

    // Detect if the list is scrolling
    var isScrolling by remember { mutableStateOf(false) }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrollInProgress ->
                isScrolling = isScrollInProgress
            }
    }

    val scrollThreshold = 100f // Define your scroll threshold here

    // Determine if the current scroll position is beyond the threshold
    val isFabCollapsed by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex >= 0.1
        }
    }

    val scrollFraction by remember {
        derivedStateOf {
            if (scrollBehavior.state.collapsedFraction > scrollThreshold / TopAppBarDefaults.MediumAppBarExpandedHeight.value) {
                (scrollBehavior.state.collapsedFraction - scrollThreshold / TopAppBarDefaults.MediumAppBarExpandedHeight.value) /
                        (1f - scrollThreshold / TopAppBarDefaults.MediumAppBarExpandedHeight.value)
            } else {
                0f
            }
        }
    }

    // Animate alpha based on scroll
    val imageAlpha by remember {
        derivedStateOf {
            0.5f + (1f - scrollFraction) * 0.2f
        }
    }

    // Calculate avatar image alpha based on scroll
    val avatarAlpha by remember {
        derivedStateOf {
            // Fade out the image as you scroll. You can adjust the scaling factor if needed.
            1f - scrollFraction
        }
    }

    val revAvatarAlpha by remember {
        derivedStateOf {
            // Fade out the image as you scroll. You can adjust the scaling factor if needed.
            0f + scrollFraction
        }
    }

    BackHandler {
        navController.popBackStack(Screen.GroupScreen.route, inclusive = true)
        navController.navigate(Screen.GroupScreen.route)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Background
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = groupInfo.value.data?.imageUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TopAppBarDefaults.MediumAppBarExpandedHeight - (15.dp * scrollBehavior.state.collapsedFraction))
                        .alpha(imageAlpha)
                )

                // Avatar Image with Border
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 60.dp, y = 10.dp)
                        .alpha(avatarAlpha) // Apply alpha to the entire Box
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = groupInfo.value.data?.imageUrl)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        ),
                        contentDescription = "Group Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20))
                            .border(
                                width = 4.dp,
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(20)
                            )
                    )
                }

                MediumTopAppBar(
                    title = {
                        Text(
                            text = groupInfo.value.data?.name ?: "Group Name",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(revAvatarAlpha),
                            color = Color.White // Change text color for better contrast with background image
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack(Screen.GroupScreen.route, inclusive = true)
                            navController.navigate(Screen.GroupScreen.route)
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.GroupSettingScreen.createRoute(groupId)) }) {
                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "group setting")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarColors(
                        Color.Transparent,
                        Color.Transparent,
                        Color.White,
                        Color.Transparent,
                        Color.White
                    )
                )
            }
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
                    GroupTransactions(transactionViewModel, transactions, tokenManager, userViewModel, navController, groupInfo, avatarAlpha, calculateDebt, scrollState, groupViewModel)
                }

                Crossfade(targetState = isFabCollapsed, modifier = Modifier.align(Alignment.BottomEnd)) { scrolling ->
                    if (scrolling) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.AddExpenseScreen.createRoute(groupId))
                            }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Expense")
                        }
                    } else {
                        ExtendedFloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.AddExpenseScreen.createRoute(groupId))
                            },
                            text = { Text("Add Expense") },
                            icon = { Icon(Icons.Filled.Add, contentDescription = "Add Expense") }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupTransactions(
    transactionViewModel: TransactionViewModel,
    transactions: State<NetworkResult<List<GetTransactionsByGroupResponse>>>,
    tokenManager: TokenManager,
    userViewModel: UserViewModel,
    navController: NavController,
    groupInfo: State<NetworkResult<AddGroupResponse>>,
    avatarAlpha: Float,
    calculateDebt: NetworkResult<CalculateDebtResponse>,
    scrollState: LazyListState,
    groupViewModel: GroupViewModel
) {

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
        // Simulate a network refresh or other long-running operation
        coroutineScope.launch {
            transactionViewModel.getTransactionsByUser(groupId = groupInfo.value.data?.groupId.toString())
            delay(500)
            isRefreshing = false
        }
    })

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pullRefresh(pullRefreshState) // Enable pull-to-refresh on this Box
//    ) {
//        PullRefreshIndicator(
//            refreshing = isRefreshing,
//            state = pullRefreshState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 8.dp) // Adjust padding as needed
//                .zIndex(1f)
//        )
//        Column()
//        {
            LazyColumn(
                state = scrollState
            ){
                item {
                    GroupInfo(
                        navController = navController,
                        data = groupInfo.value.data,
                        avatarAlpha,
                        calculateDebt,
                        groupViewModel
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
                transactions.value.data?.let { transactionList ->
                    items(transactionList) { transaction ->
                        TransactionItem(transaction, tokenManager, navController)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(250.dp))
                }
            }
//        }
//    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItem(
    transaction: GetTransactionsByGroupResponse,
    tokenManager: TokenManager,
    navController: NavController
) {
    val context = LocalContext.current
    Card (
        onClick = {
            if (transaction.category == null && transaction.description == null){
                Toast.makeText(context, "You clicked settle up transaction", Toast.LENGTH_SHORT).show()
            } else {
                navController.navigate(Screen.DetailedTransactionScreen.createRoute(transaction.transactionId))
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp, 1.dp, 5.dp, 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ){
        Box {
            if (transaction.description == null && transaction.category == null){
                SettleUpTransaction(transaction, tokenManager)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center){
                        val formattedDate = UtilMethods.formatDate(transaction.createdOn)
                        Text(
                            text = formattedDate, modifier = Modifier
                                .padding(5.dp, 0.dp, 0.dp, 0.dp),
                            textAlign = TextAlign.Justify,
                            fontSize = 14.sp
                        )
                    }

                    // Image Box
                    Box(
                        modifier = Modifier
                            .weight(0.6f) // Adjust the weight to control the space occupied by the image
                            .align(Alignment.CenterVertically)
                            .padding(start = 3.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = if (transaction.category.imageUrl.isNullOrBlank()) "" else transaction.category.imageUrl)
                                    .apply {
                                        crossfade(true)
                                    }.build()
                            ),
                            contentDescription = "Category Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp) // Set the size of the image
                                .clip(RoundedCornerShape(20))
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(20)
                                )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(text = transaction.description)
                        Text(text = buildAnnotatedString {
                            if (transaction.userUuid == tokenManager.getUserUuid().toString()) {
                                append("You paid ${UtilMethods.formatAmount(transaction.amount)}")
                            } else {
                                append("${UtilMethods.abbreviateName(transaction.payerName)} paid ${UtilMethods.formatAmount(transaction.amount)}")
                            }
                        }, fontSize = 12.sp)
                    }

                    Column(modifier = Modifier
                        .weight(1.5f)
                        .align(Alignment.CenterVertically)
                    ) {
                        Text(text = buildAnnotatedString {
                            if (transaction.loggedInUserTransaction == null) {
                                withStyle(style = SpanStyle(color = Grey400)){
                                    append("not involved")
                                }
                            } else {
                                if (transaction.loggedInUserTransaction.owedOrLent.equals("OWED")) {
                                    withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
//                                        append("you borrowed\n"+UtilMethods.formatAmount(transaction.loggedInUserTransaction.amount))
                                        append("you borrowed")
                                    }
                                } else {
                                    withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
//                                        append("you lent\n"+UtilMethods.formatAmount(transaction.loggedInUserTransaction.amount))
                                        append("you lent")
                                    }
                                }
                            }
                        }, modifier = Modifier.align(Alignment.End), fontSize = 12.sp)
                        if (transaction.loggedInUserTransaction != null) {
                            Text(
                                text = buildAnnotatedString {
                                    if (transaction.loggedInUserTransaction.owedOrLent.equals("OWED"))
                                        withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)) {
                                            append(UtilMethods.formatAmount(transaction.loggedInUserTransaction.amount))
                                        }
                                    else
                                        withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)) {
                                            append(UtilMethods.formatAmount(transaction.loggedInUserTransaction.amount))
                                        }
                                },
                                style = TextStyle(color = MaterialTheme.colors.onSurface),
                                modifier = Modifier.align(Alignment.End),
                                fontSize = 14.sp
                            )
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
    data: AddGroupResponse?,
    avatarAlpha: Float,
    calculateDebt: NetworkResult<CalculateDebtResponse>,
    groupViewModel: GroupViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    data?.let {
        Text(modifier = Modifier
            .padding(60.dp, 20.dp, 0.dp, 0.dp)
            .alpha(avatarAlpha), text = data.name)

        UserDebt(calculateDebt)

        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
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
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { scope.launch { groupViewModel.downloadExcel(context, groupId = data.groupId) } }) {
                Text(text = "Export")
            }
        }
    }
}

@Composable
fun SettleUpTransaction(transaction: GetTransactionsByGroupResponse, tokenManager: TokenManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center){
            val formattedDate = UtilMethods.formatDate(transaction.createdOn)
            Text(
                text = formattedDate, modifier = Modifier
                    .padding(5.dp, 0.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Justify,
                fontSize = 14.sp
            )
        }

        // Image Box
        Box(
            modifier = Modifier
                .weight(0.6f) // Adjust the weight to control the space occupied by the image
                .align(Alignment.CenterVertically)
                .padding(start = 3.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.money),
                contentDescription = "Category Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp) // Set the size of the image
                    .clip(RoundedCornerShape(20))
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(20)
                    )
            )
        }

        Column(
            modifier = Modifier
                .weight(2.5f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = buildAnnotatedString {
                if (transaction.userUuid == tokenManager.getUserUuid().toString()) {
                    append("You paid ${UtilMethods.formatAmount(transaction.amount)} to ${transaction.receiverName}")
                } else {
                    append("${UtilMethods.abbreviateName(transaction.payerName)} paid ${UtilMethods.formatAmount(transaction.amount)} to ${UtilMethods.abbreviateName(transaction.receiverName)}")
                }
            }, fontSize = 12.sp)
        }
    }
}

@Composable
fun UserDebt(calculateDebt: NetworkResult<CalculateDebtResponse>) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val loggedInUserUuid = tokenManager.getUserUuid().toString()

    Column(
        modifier = Modifier
            .padding(45.dp, 5.dp, 0.dp, 0.dp)
    ) {
        when (calculateDebt) {
            is NetworkResult.Error -> {
                Text(text = "Error fetching data")
            }
            is NetworkResult.Idle -> {
                Text(text = "Idle state")
            }
            is NetworkResult.Loading -> {
                Text(text = "Loading...")
            }
            is NetworkResult.Success -> {
                val debtData = (calculateDebt as NetworkResult.Success).data
                debtData?.let { data ->
                    // Find if the user is a creditor or debtor
                    val creditor = data.creditorList.find { it.uuid == loggedInUserUuid }
                    val debtor = data.debtorList.find { it.uuid == loggedInUserUuid }

                    when {
                        creditor != null -> {
                            // Display creditor information
                            val totalLent = creditor.getsBack
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                                        append("You are owed ${UtilMethods.formatAmount(totalLent)} overall")
                                    }
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                            val displayedDetails = creditor.lentTo.take(3)
                            displayedDetails.forEach {
                                Text(
                                    text = buildAnnotatedString {
                                        append("${UtilMethods.abbreviateName(it.name)} owes you ")
                                        withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                                            append(UtilMethods.formatAmount(it.amount))
                                        }
                                    },
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                            if (creditor.lentTo.size > 3) {
                                Text(
                                    text = "Plus other balances",
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                        debtor != null -> {
                            // Display debtor information
                            val totalOwed = debtor.totalReturnAmount
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                                        append("You owe ${UtilMethods.formatAmount(totalOwed)} overall")
                                    }
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )

                            val displayedDetails = debtor.lentFrom.take(3)
                            displayedDetails.forEach {
                                Text(
                                    text = buildAnnotatedString {
                                        append("You owe ${UtilMethods.abbreviateName(it.name)}")
                                        withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                                            append(" " + UtilMethods.formatAmount(it.amount))
                                        }
                                    },
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                            if (debtor.lentFrom.size > 3) {
                                Text(
                                    text = "Plus other balances",
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                        else -> {
                            Text(text = "You are all settled up in this group.", modifier = Modifier.padding(start = 15.dp))
                        }
                    }
                }
            }
        }
    }
}
