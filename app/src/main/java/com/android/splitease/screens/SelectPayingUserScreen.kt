package com.android.splitease.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPayingUserScreen(navController: NavController,
                           groupViewModel: GroupViewModel = hiltViewModel(),
                           groupId: Int) {
    LaunchedEffect(Unit) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    val groupMembers by groupViewModel.groupMembersV2.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Select Payer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ){ padding ->
        when (groupMembers) {
            is NetworkResult.Success -> {
                val members = (groupMembers as NetworkResult.Success).data
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(members!!.toList()) { member ->
                        Log.d("memberData", "SelectPayingUserScreen: ${member.name}")
                        Text(
                            text = member.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "selectedUserName",
                                        member.name
                                    )
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "selectedUserUuid",
                                        member.userUuid
                                    )
                                    Log.d(
                                        "SelectPayingUserScreen",
                                        "Selected user name: ${member.userUuid}"
                                    )
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
}