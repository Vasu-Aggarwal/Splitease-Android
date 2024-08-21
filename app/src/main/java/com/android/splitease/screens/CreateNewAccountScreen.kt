package com.android.splitease.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.splitease.models.requests.RegisterUserRequest
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.models.responses.UserLoginResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.LoginViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewAccountScreen(navController: NavController, userViewModel: UserViewModel = hiltViewModel(), loginViewModel: LoginViewModel = hiltViewModel()) {

    val isUserExists: State<NetworkResult<List<GetUserByUuidResponse>>> = userViewModel.isUserExists.collectAsState()
    val registerUser by loginViewModel.registerUser.collectAsState()
    val user by loginViewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var passwordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Handle registration and login
    LaunchedEffect(registerUser) {
        if (registerUser is NetworkResult.Success) {
            loginViewModel.login(email, password)
        }
    }

    LaunchedEffect(user) {
        if (user is NetworkResult.Success) {
            navController.navigate(Screen.BottomNavigationBar.route)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create new account",
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
                coroutineScope.launch {
                    var valid = true

                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                        valid = false
                    } else {
                        nameError = ""
                    }

                    if (email.isBlank()) {
                        emailError = "Email cannot be empty"
                        valid = false
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email address"
                        valid = false
                    } else {
                        emailError = ""
                    }

                    if (password.isBlank()) {
                        passwordError = "Password cannot be empty"
                        valid = false
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        valid = false
                    } else {
                        passwordError = ""
                    }

                    if (valid) {
                        // Prepare the request object
                        val registerUserRequest = RegisterUserRequest(
                            "",
                            name,
                            password,
                            email,
                            phone.takeIf { it.isNotBlank() } ?: ""
                        )

                        // Start registration
                        loginViewModel.registerUser(registerUserRequest)
                    }
                }

                Thread.sleep(500)
                loginViewModel.login(email, password)
                when (user) {
                    is NetworkResult.Idle -> {

                    }

                    is NetworkResult.Loading -> {

                    }
                    is NetworkResult.Success -> {
                        navController.navigate(Screen.BottomNavigationBar.route)
                    }

                    is NetworkResult.Error -> {
                    }

                    else -> {}
                }
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

            if (nameError.isNotEmpty()) {
                Text(text = nameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Name", fontSize = 14.sp) },
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
                ),
                isError = nameError.isNotEmpty(),
                trailingIcon = {
                    if (nameError.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            if (emailError.isNotEmpty()) {
                Text(text = emailError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                ),
                isError = emailError.isNotEmpty(),
                trailingIcon = {
                    if (emailError.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )


            TextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("Phone Number (Optional)", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            if (passwordError.isNotEmpty()) {
                Text(text = passwordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", fontSize = 14.sp) },
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
                ),
                isError = passwordError.isNotEmpty(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (passwordError.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    } else {
                        if (!password.isNullOrBlank()){
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
    }
}