package com.android.splitease.screens

import android.util.Log
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
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import com.android.splitease.utils.SplitBy
import com.android.splitease.viewmodels.GroupViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import kotlin.math.abs


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitMethodsScreen(navController: NavController, groupViewModel: GroupViewModel = hiltViewModel(), groupId: Int, amount: Double) {

    LaunchedEffect(Unit) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    val groupMembers by groupViewModel.groupMembersV2.collectAsState()
    val pagerState = rememberPagerState()

    // State to hold selected data from each page
    val selectedData = remember { mutableStateMapOf<String, Double>() }
    val selectedDataUnequal = remember { mutableStateMapOf<String, Double>() }
    val selectedDataByPercentage = remember { mutableStateMapOf<String, Double>() }
    val selectedDataByShares = remember { mutableStateMapOf<String, Double>() }

    // State to control the visibility of the alert dialog
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.wrapContentHeight(),
                title = { Text("Adjust split") },
                actions = {
                    IconButton(onClick = {
                        // Collect data from the current page and navigate to the next screen
                        when(pagerState.currentPage){
                            0 -> {
                                val data = getSelectedDataForCurrentPage(
                                    pagerState.currentPage,
                                    selectedData,
                                    amount
                                )
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedData", data)
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedSplitBy", SplitBy.EQUAL)
                                navController.popBackStack()
                            }
                            1 -> {
                                val totalAssignedAmount = selectedDataUnequal.values.sum()
                                if (totalAssignedAmount > amount) {
                                    // Show an alert if the total assigned amount exceeds the total amount
                                    alertMessage = "The total assigned amount exceeds the available amount."
                                    showAlert = true
                                } else {
                                    val data = getSelectedDataForCurrentPage(pagerState.currentPage, selectedDataUnequal, amount)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedData", data)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedSplitBy", SplitBy.UNEQUAL)
                                    navController.popBackStack()
                                }
                            }
                            2 -> {
                                val data = getSelectedDataForCurrentPage(pagerState.currentPage, selectedDataByPercentage, amount)
                                val totalPercentage = selectedDataByPercentage.values.sum()
                                if (totalPercentage > 100) {
                                    // Show an alert if the total percentage exceeds 100%
                                    alertMessage = "The total percentage assigned exceeds by ${abs(100.0 - totalPercentage)}%"
                                    showAlert = true
                                } else {
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedData", data)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedSplitBy", SplitBy.PERCENTAGE)
                                    navController.popBackStack()
                                }
                            }
                            3 -> {
                                val data = getSelectedDataForCurrentPage(
                                    pagerState.currentPage,
                                    selectedDataByShares,
                                    amount
                                )
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedData", data)
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedSplitBy", SplitBy.SHARE)
                                navController.popBackStack()
                            }
                        }
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
                    0 -> SplitEquallyScreen(groupMembers, amount, selectedData)
                    1 -> SplitUnequallyScreen(groupMembers, amount, selectedDataUnequal)
                    2 -> SplitByPercentageScreen(groupMembers, amount, selectedDataByPercentage)
                    3 -> SplitBySharesScreen(groupMembers, amount, selectedDataByShares)
                }
            }

            // Show AlertDialog if necessary
            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { showAlert = false },
                    title = { Text("Invalid Input") },
                    text = { Text(alertMessage) },
                    confirmButton = {
                        Button(onClick = { showAlert = false }) {
                            Text("OK")
                        }
                    }
                )
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
fun SplitEquallyScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedData: MutableMap<String, Double>) {
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
        SplitEqually(groupMembers, amount, selectedData)
    }
}

@Composable
fun SplitUnequallyScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataUnequal: MutableMap<String, Double>
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
        SplitMembersListUnequal(groupMembers, amount, selectedDataUnequal)
    }
}

@Composable
fun SplitByPercentageScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataByPercentage: MutableMap<String, Double>
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
        SplitMembersListByPercentage(groupMembers, amount, selectedDataByPercentage)
    }
}

@Composable
fun SplitBySharesScreen(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataByShares: MutableMap<String, Double>
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
        SplitMembersListByShares(groupMembers, amount, selectedDataByShares)
    }
}

@Composable
fun SplitEqually(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedData: MutableMap<String, Double>
) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data

            // Ensure a unique identifier (like member ID) is used for state management
            val checkedStates = remember(members) {
                mutableStateMapOf<String, Boolean>().apply {
                    members?.forEach { this[it.userUuid] = true }
                }
            }

            // Initialize selectedData with initial checked members
            LaunchedEffect(members) {
                val checkedCount = checkedStates.count { it.value }
                val amountPerPerson = if (checkedCount > 0) amount / checkedCount else 0.0
                members?.forEach { member ->
                    if (checkedStates[member.userUuid] == true) {
                        selectedData["username_" + member.email] = amountPerPerson
                    } else {
                        selectedData.remove("username_" + member.email)
                    }
                }
            }

            // Calculate the amount per person dynamically
            val checkedCount by remember {
                derivedStateOf { checkedStates.count { it.value } }
            }

            val amountPerPerson by remember(checkedCount) {
                derivedStateOf {
                    if (checkedCount > 0) amount / checkedCount else 0.0
                }
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
                                if (isChecked) {
                                    selectedData["username_" + member.email] = amountPerPerson
                                } else {
                                    selectedData.remove("username_" + member.email)
                                }
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
            }
        }
        is NetworkResult.Error -> {
            Text(
                text = "Failed to fetch group members",
                color = Color.White
            )
        }
        is NetworkResult.Loading -> {
            CircularProgressIndicator(
                color = Color.White
            )
        }
        is NetworkResult.Idle -> Text(text = "Idle")
    }
}

@Composable
fun SplitMembersListUnequal(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataUnequal: MutableMap<String, Double>
) {
    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            val individualAmounts = remember { mutableStateMapOf<String, String>() }

            // Initialize selectedData with initial values from individualAmounts
            LaunchedEffect(members) {
                individualAmounts.forEach { (uuid, amountStr) ->
                    val member = members!!.find { it.userUuid == uuid }
                    if (member != null) {
                        val amountValue = amountStr.toDoubleOrNull()
                        if (amountValue != null) {
                            selectedDataUnequal["username_" + member.email] = amountValue
                        } else {
                            selectedDataUnequal.remove("username_" + member.email)
                        }
                    }
                }
            }

            val totalAmount by remember {
                derivedStateOf {
                    individualAmounts.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                }
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
                        OutlinedTextField(
                            value = individualAmounts[member.userUuid] ?: "",
                            onValueChange = { newAmount ->
                                individualAmounts[member.userUuid] = newAmount
                                val amountValue = newAmount.toDoubleOrNull()
                                if (amountValue != null && amountValue > 0.0) {
                                    selectedDataUnequal["username_" + member.email] = amountValue
                                } else {
                                    selectedDataUnequal.remove("username_" + member.email)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions.Default,
                            singleLine = true,
                            modifier = Modifier.width(100.dp)
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
                Text(text = "Total assigned: ₹${String.format("%.2f", totalAmount)}", color = Color.White)
                Text(text = "Remaining: ₹${String.format("%.2f", amount - totalAmount)}", color = Color.White)
            }
        }
        is NetworkResult.Error -> {
            Text(
                text = "Failed to fetch group members",
                color = Color.White
            )
        }
        is NetworkResult.Loading -> {
            CircularProgressIndicator(
                color = Color.White
            )
        }

        is NetworkResult.Idle -> Text(text = "Idle")
    }
}

@Composable
fun SplitMembersListByPercentage(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataByPercentage: MutableMap<String, Double>
) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            val individualPercentages = remember { mutableStateMapOf<String, String>() }

            // Initialize selectedData with initial values from individualPercentages
            LaunchedEffect(members) {
                individualPercentages.forEach { (uuid, percentageStr) ->
                    val member = members!!.find { it.userUuid == uuid }
                    if (member != null) {
                        val percentage = percentageStr.toDoubleOrNull() ?: 0.0
                        selectedDataByPercentage["username_" + member.email] = percentage
                    }
                }
            }

            val totalPercentage by remember {
                derivedStateOf {
                    individualPercentages.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                }
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
                        OutlinedTextField(
                            value = individualPercentages[member.userUuid] ?: "",
                            onValueChange = { newPercentage ->
                                individualPercentages[member.userUuid] = newPercentage
                                val percentage = newPercentage.toDoubleOrNull() ?: 1.0
                                selectedDataByPercentage["username_" + member.email] = percentage
                                // Remove the user from selectedData if the percentage is zero
                                if (percentage == 0.0) {
                                    selectedDataByPercentage.remove("username_" + member.email)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions.Default,
                            singleLine = true,
                            modifier = Modifier.width(100.dp)
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
                Text(text = "Total assigned: ${String.format("%.2f", totalPercentage)}%", color = Color.White)
                Text(text = "Remaining: ${String.format("%.2f", 100 - totalPercentage)}%", color = Color.White)
            }
        }
        is NetworkResult.Error -> {
            Text(
                text = "Failed to fetch group members",
                color = Color.White
            )
        }
        is NetworkResult.Loading -> {
            CircularProgressIndicator(
                color = Color.White
            )
        }

        is NetworkResult.Idle -> Text(text = "Idle")
    }
}

@Composable
fun SplitMembersListByShares(
    groupMembers: NetworkResult<List<GetGroupMembersV2Response>>,
    amount: Double,
    selectedDataByShares: MutableMap<String, Double>
) {
    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            val individualShares = remember { mutableStateMapOf<String, String>() }

            // Initialize selectedData with initial values from individualShares
            LaunchedEffect(members) {
                individualShares.forEach { (uuid, sharesStr) ->
                    val member = members!!.find { it.userUuid == uuid }
                    if (member != null) {
                        val shares = sharesStr.toDoubleOrNull() ?: 0.0
                        selectedDataByShares["username_" + member.email] = shares
                    }
                }
            }

            val totalShares by remember {
                derivedStateOf {
                    individualShares.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                }
            }

            val singleSharePrice = if (totalShares > 0) amount / totalShares else 0.0

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
                        OutlinedTextField(
                            value = individualShares[member.userUuid] ?: "",
                            onValueChange = { newShares ->
                                individualShares[member.userUuid] = newShares
                                val shares = newShares.toDoubleOrNull() ?: 0.0
                                val newTotalShares = individualShares.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                                val updatedSingleSharePrice = if (newTotalShares > 0) amount / newTotalShares else 0.0
                                selectedDataByShares["username_" + member.email] = shares * updatedSingleSharePrice

                                // Update all values in selectedDataByShares
                                individualShares.forEach { (uuid, sharesStr) ->
                                    val mem = members.find { it.userUuid == uuid }
                                    if (mem != null) {
                                        val shareValue = sharesStr.toDoubleOrNull() ?: 0.0
                                        selectedDataByShares["username_" + mem.name] = shareValue * updatedSingleSharePrice
                                    }
                                }

                                // Remove the user from selectedData if the shares are zero
                                if (shares == 0.0) {
                                    selectedDataByShares.remove("username_" + member.email)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions.Default,
                            singleLine = true,
                            modifier = Modifier.width(100.dp)
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
                Text(text = "Total shares: ${String.format("%.2f", totalShares)}", color = Color.White)
                Text(text = "Single share price: ₹${String.format("%.2f", singleSharePrice)}", color = Color.White)
            }
        }
        is NetworkResult.Error -> {
            Text(
                text = "Failed to fetch group members",
                color = Color.White
            )
        }
        is NetworkResult.Loading -> {
            CircularProgressIndicator(
                color = Color.White
            )
        }

        is NetworkResult.Idle -> Text(text = "Idle")
    }
}



private fun getSelectedDataForCurrentPage(currentPage: Int, dataMap: SnapshotStateMap<String, Double>, amount: Double): Map<String, Double> {
    return when (currentPage) {
        0 -> {
            // Split Equally
            val totalAmount = amount
            val userNames = dataMap.filterKeys { it.startsWith("username_") } // Filter user names
            val amountPerPerson = if (userNames.isNotEmpty()) totalAmount / userNames.size else 0.0

            userNames.mapKeys { it.key.removePrefix("username_") } // Remove prefix if necessary
                .mapValues { amountPerPerson }
        }
        1 -> {
            // Split Unequally
            val userNames = dataMap.filterKeys { it.startsWith("username_") }
            userNames.mapKeys { it.key.removePrefix("username_") }
                .mapValues { it.value }
        }
        2 -> {
            // Split by Percentages
            val userNames = dataMap.filterKeys { it.startsWith("username_") }
            userNames.mapKeys { it.key.removePrefix("username_") }
                .mapValues { it.value }
        }
        3 -> {
            // Split by Shares
            val userShares = dataMap.filterKeys { it.startsWith("username_") }
            val totalShares = userShares.values.sum()
            userShares.mapKeys { it.key.removePrefix("username_") } // Remove prefix if necessary
                .mapValues { entry -> if (totalShares > 0) (entry.value / totalShares) * amount else 0.0 }
        }
        else -> {
            emptyMap()
        }
    }
}