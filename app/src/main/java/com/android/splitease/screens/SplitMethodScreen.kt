package com.android.splitease.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitMethodsScreen(navController: NavController, groupViewModel: GroupViewModel = hiltViewModel(), groupId: Int, amount: Double) {

    LaunchedEffect(Unit) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    val groupMembers by groupViewModel.groupMembersV2.collectAsState()
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.wrapContentHeight(),
                title = { Text("Adjust split") },
                actions = {
                    IconButton(onClick = {
                        // Collect data from the current page and navigate to the next screen
                        val selectedData = getSelectedDataForCurrentPage(pagerState.currentPage)
//                        navController.navigate("next_screen_route/${selectedData}")
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Tabs(pagerState = pagerState)
            HorizontalPager(count = 4, state = pagerState, userScrollEnabled = true) { page ->
                when (page) {
                    0 -> SplitEquallyScreen(groupMembers, amount)
                    1 -> SplitUnequallyScreen(groupMembers, amount)
                    2 -> SplitByPercentageScreen(groupMembers, amount)
                    3 -> SplitBySharesScreen(groupMembers, amount)
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val tabs = listOf("Equally", "Unequally", "By percentages", "By Shares")
    val scope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        edgePadding = 0.dp
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Composable
fun SplitEquallyScreen(groupMembers: NetworkResult<List<GetGroupMembersV2Response>>, amount: Double) {
    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Split equally",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select which people owe an equal share.",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SplitMembersList(groupMembers, amount)
    }
}

@Composable
fun SplitUnequallyScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Split unequally",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Adjust the amount each person owes.",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SplitMembersListUnequal(groupMembers, amount)
    }
}

@Composable
fun SplitByPercentageScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Split by percentages",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Assign percentages to each person.",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SplitMembersListByPercentage(groupMembers, amount)
    }
}

@Composable
fun SplitBySharesScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Split by percentages",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Assign percentages to each person.",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SplitMembersList(groupMembers, amount)
    }
}

@Composable
fun SplitMembersList(groupMembers: NetworkResult<List<GetGroupMembersV2Response>>, amount: Double) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            // Ensure a unique identifier (like member ID) is used for state management
            val checkedStates = remember {
                mutableStateMapOf<String, Boolean>().apply {
                    members!!.forEach { this[it.userUuid] = true }
                }
            }

            // Calculate the amount per person dynamically
            val checkedCount by remember {
                derivedStateOf { checkedStates.count { it.value } }
            }

            val amountPerPerson by remember(checkedCount) {
                derivedStateOf { if (checkedCount > 0) amount / checkedCount else 0.0 }
            }

            LazyColumn {
                items(members!!.toList()) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.name,
                            modifier = Modifier.weight(1f),
                            color = Color.White
                        )
                        Checkbox(
                            checked = checkedStates[member.userUuid] ?: false,
                            onCheckedChange = { isChecked ->
                                checkedStates[member.userUuid] = isChecked
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "₹${String.format("%.2f", amountPerPerson)}/person", color = Color.White)
                Text(text = "(${checkedCount} people)", color = Color.White)
                Checkbox(
                    checked = checkedStates.values.all { it },
                    onCheckedChange = { isChecked ->
                        checkedStates.keys.forEach { checkedStates[it] = isChecked }
                    }
                )
            }
        }
        is NetworkResult.Error -> {
            Text(text = "Error loading members")
        }
        is NetworkResult.Loading -> {
            Text(text = "Loading members...")
        }
        is NetworkResult.Idle -> {
            // Handle idle state if necessary
            Text(text = "Idle")
        }
    }
}

@Composable
fun SplitMembersListUnequal(groupMembers: NetworkResult<List<GetGroupMembersV2Response>>, amount: Double) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            // State to hold amounts entered for each user
            val amounts = remember {
                mutableStateMapOf<String, Double>().apply {
                    members!!.forEach { this[it.userUuid] } // Initialize with 0.0
                }
            }

            // Calculate total entered amount
            val totalEnteredAmount by remember {
                derivedStateOf { amounts.values.sum() }
            }

            LazyColumn {
                items(members!!.toList()) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.name,
                            modifier = Modifier.weight(1f),
                            color = Color.White
                        )
                        // TextField for entering the amount
                        TextField(
                            value = amounts[member.userUuid]?.toString() ?: "",
                            onValueChange = { newValue ->
                                val newAmount = newValue.toDoubleOrNull()!!
                                amounts[member.userUuid] = newAmount
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = { /* Handle Done action if needed */ }),
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "₹${String.format("%.2f", totalEnteredAmount)}/total", color = Color.White)
                Text(text = "(${members!!.size} people)", color = Color.White)
            }
        }
        is NetworkResult.Error -> {
            Text(text = "Error loading members")
        }
        is NetworkResult.Loading -> {
            Text(text = "Loading members...")
        }
        is NetworkResult.Idle -> {
            // Handle idle state if necessary
            Text(text = "Idle")
        }
    }
}

@Composable
fun SplitMembersListByPercentage(groupMembers: NetworkResult<List<GetGroupMembersV2Response>>, amount: Double) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data

            // State to hold percentages entered for each user
            val percentages = remember {
                mutableStateMapOf<String, Double>().apply {
                    members!!.forEach { this[it.userUuid] } // Initialize with 0.0
                }
            }

            // Calculate total entered percentage
            val totalEnteredPercentage by remember {
                derivedStateOf { percentages.values.sum() }
            }

            // Determine if the total entered percentage exceeds 100%
            val isExceeded = totalEnteredPercentage > 100

            LazyColumn {
                items(members!!.toList()) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.name,
                            modifier = Modifier.weight(1f),
                            color = Color.White
                        )
                        // TextField for entering the percentage
                        TextField(
                            value = percentages[member.userUuid]?.toString() ?: "",
                            onValueChange = { newValue ->
                                val newPercentage = newValue.toDoubleOrNull() ?: 0.0
                                // Update percentage if total does not exceed 100%
                                if (totalEnteredPercentage - (percentages[member.userUuid] ?: 0.0) + newPercentage <= 100) {
                                    percentages[member.userUuid] = newPercentage
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = { /* Handle Done action if needed */ }),
                            modifier = Modifier.width(120.dp),
                            isError = isExceeded // Set error state if total exceeds 100%
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExceeded) "Total exceeds 100%" else "₹${String.format("%.2f", amount)} total",
                    color = if (isExceeded) Color.Red else Color.White
                )
                Text(text = "(${members!!.size} people)", color = Color.White)
            }
        }
        is NetworkResult.Error -> {
            Text(text = "Error loading members")
        }
        is NetworkResult.Loading -> {
            Text(text = "Loading members...")
        }
        is NetworkResult.Idle -> {
            // Handle idle state if necessary
            Text(text = "Idle")
        }
    }
}


fun getSelectedDataForCurrentPage(currentPage: Int): String {
    // Logic to collect data from the current page
    // For example, return a JSON string or any data format you need
    return when (currentPage) {
        0 -> "data from Equally"
        1 -> "data from Unequally"
        2 -> "data from By percentages"
        3 -> "data from By Shares"
        else -> "no data"
    }
}
