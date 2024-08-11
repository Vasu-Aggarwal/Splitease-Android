package com.android.splitease.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.TokenManager

@Composable
fun AccountScreen(navController: NavController) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        Button(
            onClick = {
                tokenManager.clearTokens()
                navController.navigate(Screen.LoginScreen.route) {
                    // This clears the entire back stack
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    // Ensure that the back stack is completely cleared
                    launchSingleTop = true
                }
            },
        ) {
            Text(text = "Logout")
        }
    }
}
