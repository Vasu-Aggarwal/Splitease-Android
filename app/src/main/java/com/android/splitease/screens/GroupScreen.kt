package com.android.splitease.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.splitease.R
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.models.responses.GetOverallUserBalance
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Black
import com.android.splitease.ui.theme.Grey400
import com.android.splitease.ui.theme.Grey800
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.ErrorDialog
import com.android.splitease.utils.LoadingOverlay
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GroupScreen(viewModel: GroupViewModel = hiltViewModel(), navController: NavController, userViewModel: UserViewModel = hiltViewModel()) {
    val groups: State<NetworkResult<List<GetGroupsByUserResponse>>> = viewModel.groups.collectAsState()
    val userBalance: State<NetworkResult<GetOverallUserBalance>> = userViewModel.userBalance.collectAsState()

    //variables for filter popup menu
    var showMenu by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
        // Simulate a network refresh or other long-running operation
        coroutineScope.launch {
            userViewModel.getOverallUserBalance("")
            viewModel.getGroupsByUser("")
            delay(500)
            isRefreshing = false
        }
    })

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorTitle = remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getGroupsByUser("")
        userViewModel.getOverallUserBalance("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.titleLarge, // Adjust text style if needed
                    )
                },
                navigationIcon = { /* TODO */ },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.NewGroupScreen.createRoute("add", 0)) }) {
                        Icon(painter = painterResource(id = R.drawable.add_people), contentDescription = "Settings")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.11f)
            )
        }
    ){ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState) // Enable pull-to-refresh on this Box
            ) {
                Divider(color = Black)
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp) // Adjust padding as needed
                        .zIndex(1f)
                )
                Column()
                {
                    LazyColumn(modifier = Modifier.fillMaxSize()) { // Adjust padding as needed
                        item {
                            when (val result = userBalance.value) {
                                is NetworkResult.Success -> {
                                    loading = false
                                    showErrorDialog = false
                                    val balance = result.data!!.netBalance
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .padding(4.dp, 4.dp, 4.dp, 0.dp)
                                            .height(60.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .align(Alignment.CenterVertically),
                                                text = buildAnnotatedString {
                                                    when {
                                                        balance < 0 -> {
                                                            append("Overall, you are owed ")
                                                            withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)) {
                                                                append(
                                                                    UtilMethods.formatAmount(
                                                                        abs(
                                                                            balance
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }

                                                        balance > 0 -> {
                                                            append("Overall, you owe ")
                                                            withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)) {
                                                                append(
                                                                    UtilMethods.formatAmount(
                                                                        abs(
                                                                            balance
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        }

                                                        else -> ""
                                                    }
                                                }
                                            )

                                            Box {
                                                IconButton(
                                                    onClick = { showMenu = true },
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.filter),
                                                        contentDescription = "Filters"
                                                    )
                                                }

                                                DropdownMenu(
                                                    expanded = showMenu,
                                                    onDismissRequest = { showMenu = false },
                                                    modifier = Modifier.background(color = Grey800)
                                                ) {
                                                    val options = listOf(
                                                        "All groups",
                                                        "Groups you owe",
                                                        "Groups that owe you",
                                                        "Outstanding balances"
                                                    )
                                                    options.forEach { option ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedOption = option
                                                                showMenu = false
                                                                // Call your API with the selected option
                                                                if(option.equals("All groups")) {
                                                                    viewModel.getGroupsByUser(AppConstants.ALL_GROUPS)
                                                                    userViewModel.getOverallUserBalance(AppConstants.ALL_GROUPS)
                                                                } else if (option.equals("Groups you owe")) {
                                                                    viewModel.getGroupsByUser(AppConstants.GROUPS_YOU_OWE)
                                                                    userViewModel.getOverallUserBalance(AppConstants.GROUPS_YOU_OWE)
                                                                } else if (option.equals("Groups that owe you")) {
                                                                    viewModel.getGroupsByUser(AppConstants.GROUPS_THAT_OWE_YOU)
                                                                    userViewModel.getOverallUserBalance(AppConstants.GROUPS_THAT_OWE_YOU)
                                                                } else {
                                                                    viewModel.getGroupsByUser(AppConstants.OUTSTANDING_BALANCE)
                                                                    userViewModel.getOverallUserBalance(AppConstants.OUTSTANDING_BALANCE)
                                                                }
                                                            },
                                                            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                RadioButton(
                                                                    selected = (option == selectedOption),
                                                                    colors = RadioButtonDefaults.colors(unselectedColor = Color.White, selectedColor = AppConstants.OWE_COLOR),
                                                                    onClick = null // RadioButton click is handled by DropdownMenuItem
                                                                )
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                Text(option)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                is NetworkResult.Error -> {
                                    loading = false
                                    LaunchedEffect(Unit){
                                        errorMessage = result.message.toString()
                                        showErrorDialog = true
                                    }
                                    Text(text = "Error loading balance")
                                }

                                is NetworkResult.Loading -> {
                                    loading = true
                                    showErrorDialog = false
                                }

                                is NetworkResult.Idle -> {
                                    loading = false
                                    showErrorDialog = false
                                }
                            }
                        }
                        groups.value.data?.let { groupList ->
                            items(groupList) { group ->
                                GroupItem(group = group, viewModel, navController)
                            }
                        }
                        item {
                            StartNewGroup(navController)
                        }
                    }
                }
            }
        }
    }

    if (loading)
        LoadingOverlay()

    if (showErrorDialog){
        ErrorDialog(title = null, message = errorMessage) {
            showErrorDialog = false
        }
    }
}

@Composable
fun GroupItem(group: GetGroupsByUserResponse, viewModel: GroupViewModel, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {
            navController.navigate(Screen.DetailedGroupScreen.createRoute(group.groupId))
        },
        modifier = Modifier
            .padding(4.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            group.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = url)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(90.dp) // Adjust size as needed
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(15))
                )
            }
            Column {
                Text(text = group.name, modifier = Modifier.padding(bottom = 4.dp))
                Text(text = buildAnnotatedString {
                    if(group.userBalance<0.0) {
                        withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                            append("you are owed " + UtilMethods.formatAmount(abs(group.userBalance)))
                        }
                    } else if(group.userBalance>0.0) {
                        withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                            append("you owe " + UtilMethods.formatAmount(group.userBalance))
                        }
                    } else {
                        withStyle(style = SpanStyle(color = Grey400)){
                            append("no expenses")
                        }
                    }
                }, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
fun StartNewGroup(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        OutlinedButton(
            onClick = { navController.navigate(Screen.NewGroupScreen.createRoute("add", 0)) },
            modifier = Modifier.align(Alignment.Center),
            shape = RectangleShape
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.width(180.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_people),
                    contentDescription = "Add People"
                )
                Text(text = "Start a new group")
            }
        }
    }
}
