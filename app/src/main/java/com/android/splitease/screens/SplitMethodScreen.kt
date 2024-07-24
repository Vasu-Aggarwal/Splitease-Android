package com.android.splitease.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitMethodsScreen(navController: NavController, groupViewModel: GroupViewModel = hiltViewModel(), groupId: Int) {

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
                    0 -> SplitEquallyScreen(groupMembers)
                    1 -> SplitUnequallyScreen(groupMembers)
                    2 -> SplitByPercentageScreen(groupMembers)
                    3 -> SplitBySharesScreen(groupMembers)
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
fun SplitEquallyScreen(groupMembers: NetworkResult<Set<GetGroupMembersV2Response>>) {
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
        SplitMembersList(groupMembers)
    }
}

@Composable
fun SplitUnequallyScreen(groupMembers: NetworkResult<Set<GetGroupMembersV2Response>>) {
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
        SplitMembersList(groupMembers)
    }
}

@Composable
fun SplitByPercentageScreen(groupMembers: NetworkResult<Set<GetGroupMembersV2Response>>) {
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
        SplitMembersList(groupMembers)
    }
}

@Composable
fun SplitBySharesScreen(groupMembers: NetworkResult<Set<GetGroupMembersV2Response>>) {
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
        SplitMembersList(groupMembers)
    }
}

@Composable
fun SplitMembersList(groupMembers: NetworkResult<Set<GetGroupMembersV2Response>>) {

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = groupMembers.data
            // Ensure a unique identifier (like member ID) is used for state management
            val checkedStates = remember {
                mutableStateMapOf<String, Boolean>().apply {
                    members!!.forEach { this[it.userUuid] = true }
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
                Text(text = "₹0.00/person", color = Color.White)
                Text(text = "(${members!!.size} people)", color = Color.White)
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
