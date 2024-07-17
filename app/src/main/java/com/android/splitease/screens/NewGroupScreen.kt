package com.android.splitease.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel

@Composable
fun NewGroupScreen(
    groupViewModel: GroupViewModel = hiltViewModel(),
    navController: NavController
) {
    var groupName by remember { mutableStateOf("") }
    val addUpdateGroup : State<NetworkResult<AddGroupResponse>> = groupViewModel.addUpdateGroup.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Enter Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                groupViewModel.addUpdateGroup(AddGroupRequest(groupName))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }

        // Handling response
        when (val result = addUpdateGroup.value) {
            is NetworkResult.Success -> {
                // Navigate when success response is received
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.DetailedGroupScreen.createRoute(result.data!!.groupId)) // Replace with your target screen
                }
            }
            is NetworkResult.Error -> {
                // Handle error
                Text(text = result.message ?: "Unknown error occurred")
            }
            is NetworkResult.Loading -> {
                // Show loading state if needed
                Text(text = "Creating group...")
            }

            is NetworkResult.Idle -> Text(text = "Idle")
        }
    }
}