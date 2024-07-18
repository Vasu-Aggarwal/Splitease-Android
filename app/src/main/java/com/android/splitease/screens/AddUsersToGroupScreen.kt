package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.navigation.NavHostController
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddUsersToGroupScreen(groupId: Int, groupViewModel: GroupViewModel = hiltViewModel(), navController: NavController){
    var email by remember { mutableStateOf("") }
    var emailSet by remember { mutableStateOf(setOf<String>()) }
    val addUsersResponse by groupViewModel.addUsersToGroup.collectAsState()

    LaunchedEffect(addUsersResponse) {
        if (addUsersResponse is NetworkResult.Success) {
            val message = (addUsersResponse as NetworkResult.Success).data as? String
            if (message == "Users added successfully") {
                navController.popBackStack()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Group Id i am receiving is: $groupId")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("User Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    emailSet = emailSet + email
                    email = "" // clear the text field after adding
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Add User")
        }

        Text(text = "Users added to the group:")
        emailSet.forEach { addedEmail ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = addedEmail,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        emailSet = emailSet - addedEmail
                    }
                ) {
                    Text(text = "Delete")
                }
            }
        }

        Button(
            onClick = {
                // Here you can make the API request using emailSet
                val addUsersToGroupRequest = AddUsersToGroupRequest(groupId, emailSet)
                groupViewModel.addUsersToGroup(addUsersToGroupRequest)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Submit")
        }
    }
}