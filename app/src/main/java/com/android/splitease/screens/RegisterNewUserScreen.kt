package com.android.splitease.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.requests.RegisterUserRequest
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.LoginViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterNewUserScreen(
    name: String,
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val isUserExists: State<NetworkResult<List<GetUserByUuidResponse>>> = userViewModel.isUserExists.collectAsState()
    val registerUser: State<NetworkResult<CreateUserResponse>> = loginViewModel.registerUser.collectAsState()
    var name by remember { mutableStateOf("") }
    var emailPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add a New Contact",
                        style = MaterialTheme.typography.titleLarge, // Keep the title style consistent
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Back")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .fillMaxHeight(0.14f)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Check if the input is an email or phone
                val isPhoneNumber = emailPhone.all { it.isDigit() } || emailPhone.matches(Regex("^\\+?[0-9]*$"))

                val registerUserRequest = if (isPhoneNumber) {
                    // If it's a phone number, assign it to the last parameter
                    RegisterUserRequest(
                        "",
                        name,
                        "",
                        "",
                        emailPhone
                    )
                } else {
                    // If it's an email, assign it to the email parameter
                    RegisterUserRequest(
                        "",
                        name,
                        "",
                        emailPhone,
                        ""
                    )
                }
                loginViewModel.registerUser(registerUserRequest)
            }, shape = RoundedCornerShape(50)) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Submit")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp) // Add overall padding
        ) {
            Text(
                text = "Name",
                style = MaterialTheme.typography.bodyLarge, // Slightly larger font for labels
                modifier = Modifier.padding(bottom = 4.dp) // Space between label and text field
            )

            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Space between text fields
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant, // Surface color for better contrast
                        RoundedCornerShape(8.dp)
                    ),
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline, // Subtle outline for the text field
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "Email or Phone Number",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            TextField(
                value = emailPhone,
                onValueChange = { emailPhone = it },
                placeholder = { Text("", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ),
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
