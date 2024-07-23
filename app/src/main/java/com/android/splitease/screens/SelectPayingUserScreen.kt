package com.android.splitease.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.UserViewModel

@Composable
fun SelectPayingUserScreen(navController: NavController,
                           groupViewModel: GroupViewModel = hiltViewModel(),
                           groupId: Int) {
    LaunchedEffect(Unit) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    val groupMembers by groupViewModel.groupMembersV2.collectAsState()

    when (groupMembers) {
        is NetworkResult.Success -> {
            val members = (groupMembers as NetworkResult.Success).data
            LazyColumn {
                items(members!!.toList()) { member ->
                    Text(
                        text = member.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedUserName", member.name)
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedUserUuid", member.userUuid)
                                Log.d("SelectPayingUserScreen", "Selected user name: ${member.name}")
                                navController.popBackStack()
                            }
                            .padding(16.dp)
                    )
                }
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