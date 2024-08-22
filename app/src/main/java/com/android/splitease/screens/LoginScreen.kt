package com.android.splitease.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.UserLoginResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.ErrorDialog
import com.android.splitease.utils.LoadingOverlay
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navController: NavController){

    val user: State<NetworkResult<UserLoginResponse>> = viewModel.user.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var showLoadingOverlay by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorTitle by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (emailError.isNotEmpty()) {
            Text(text = emailError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
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
        Spacer(modifier = Modifier.height(16.dp))
        if (passwordError.isNotEmpty()) {
            Text(text = passwordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordError.isNotEmpty(),
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

                var valid = true

                if (email.isNullOrBlank()){
                    emailError = "Email cannot be empty"
                    valid = false
                } else {
                    emailError = ""
                }

                if (password.isNullOrBlank()){
                    passwordError = "Password cannot be empty"
                    valid = false
                } else {
                    passwordError = ""
                }
                if (valid) {
                    showLoadingOverlay = true
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screen.CreateAccountScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
        when (val result = user.value) {
            is NetworkResult.Idle -> {
                showLoadingOverlay = false
            }

            is NetworkResult.Loading -> {
                showLoadingOverlay = true
            }
            is NetworkResult.Success -> {
                showLoadingOverlay = false
                LaunchedEffect(Unit){
                    navController.navigate("bottomBar")
                    Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                }
            }

            is NetworkResult.Error -> {
                showLoadingOverlay = false
                LaunchedEffect(Unit){
                    errorMessage = result.message ?: "Unexpected error occurred"
                    errorTitle = "Test title"
                    showErrorDialog = true
                }
            }
        }
    }
    if (showLoadingOverlay){
        LoadingOverlay()
    }

    if (showErrorDialog){
        ErrorDialog(title = null, message = errorMessage) {
            showErrorDialog = false
        }
    }
}