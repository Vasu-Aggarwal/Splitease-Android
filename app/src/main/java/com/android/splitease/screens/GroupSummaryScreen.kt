package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSummaryScreen(
    groupId: Int,
    groupViewModel: GroupViewModel = hiltViewModel(),
    navController: NavController
){

    val groupSummary: State<NetworkResult<GetGroupSummaryResponse>> = groupViewModel.groupSummary.collectAsState()

    LaunchedEffect(groupId) {
        groupViewModel.getGroupSummary(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Group Summary") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(start = 15.dp)
        ){
            Text(text = "Group name: ${groupSummary.value.data?.groupName}")
            Text(text = "Total Group Spending: ${UtilMethods.formatAmount(groupSummary.value.data?.totalGroupSpending!!)}")
        }
    }
}