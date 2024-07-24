package com.android.splitease.screens

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
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitMethodsScreen() {
//    val pagerState = rememberPagerState()
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Tabs(pagerState = pagerState)
//        HorizontalPager(count = 4, state = pagerState, userScrollEnabled = true) { page ->
//            when (page) {
//                0 -> SplitEquallyScreen()
//                1 -> SplitUnequallyScreen()
//                2 -> SplitByPercentageScreen()
//                3 -> SplitBySharesScreen()
//            }
//        }
//    }
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
                    0 -> SplitEquallyScreen()
                    1 -> SplitUnequallyScreen()
                    2 -> SplitByPercentageScreen()
                    3 -> SplitBySharesScreen()
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
fun SplitEquallyScreen() {
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
        SplitMembersList()
    }
}

@Composable
fun SplitUnequallyScreen() {
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
        SplitMembersList()
    }
}

@Composable
fun SplitByPercentageScreen() {
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
        SplitMembersList()
    }
}

@Composable
fun SplitBySharesScreen() {
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
        SplitMembersList()
    }
}

@Composable
fun SplitMembersList() {
    val members = listOf("Vasu Aggarwal", "3", "1", "4", "2", "5")
    val checkedStates = remember { mutableStateMapOf<String, Boolean>().apply { members.forEach { this[it] = true } } }

    LazyColumn {
        items(members) { member ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = member,
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                Checkbox(
                    checked = checkedStates[member] ?: false,
                    onCheckedChange = { isChecked -> checkedStates[member] = isChecked }
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
        Text(text = "(6 people)", color = Color.White)
        Checkbox(
            checked = true,
            onCheckedChange = { /* handle All checked change */ }
        )
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
