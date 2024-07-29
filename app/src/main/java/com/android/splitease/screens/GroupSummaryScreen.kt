package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel

@Composable
fun GroupSummaryScreen(groupId: Int, groupViewModel: GroupViewModel = hiltViewModel()){

    val groupSummary: State<NetworkResult<GetGroupSummaryResponse>> = groupViewModel.groupSummary.collectAsState()

    LaunchedEffect(groupId) {
        groupViewModel.getGroupSummary(groupId)
    }

    Column{
        Text(text = "Group name: ${groupSummary.value.data?.groupName}")
        Text(text = "Total Group Spending: ${groupSummary.value.data?.totalGroupSpending}")
    }
}