package com.android.splitease.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel

@Composable
fun SettleUpPayerScreen(
    navController: NavController,
    groupViewModel: GroupViewModel = hiltViewModel(),
    groupId: Int
) {
    val groupMembers by groupViewModel.groupMembersV2.collectAsState()
    var selectedPayer: String? by remember { mutableStateOf(null) }

    // Retrieve group members when screen is composed
    LaunchedEffect(groupId) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Select Payer",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        when (groupMembers) {
            is NetworkResult.Success -> {
                val members = (groupMembers as NetworkResult.Success).data
                LazyColumn {
                    items(members!!) { member ->
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                                .clickable{
                                    navController.navigate(Screen.SettleUpReceiverScreen.createRoute(groupId, member.userUuid))
                                }
                        )
                    }
                }
            }
            is NetworkResult.Error -> {
                Text(
                    text = "Error: ${(groupMembers as NetworkResult.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is NetworkResult.Loading -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is NetworkResult.Idle -> {
                Text(
                    text = "Idle",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}