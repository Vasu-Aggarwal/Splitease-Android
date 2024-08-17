package com.android.splitease.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Blue300
import com.android.splitease.ui.theme.Red800
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods

@Composable
fun AccountScreen(navController: NavController) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)

    // Retrieve the app version
    val packageManager = context.packageManager
    val packageName = context.packageName
    val appVersion = try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(60.dp)
                    .clickable {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = "Remove user",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(30.dp)
                )
                Text(text = "Logout")
            }

            // Privacy Policy link
            val privacyPolicyUrl = "https://sites.google.com/view/splitease-privacy-policy/home?authuser=6"

            // Privacy Policy text with clickable link
            Text(
                text = buildAnnotatedString {
                    pushStringAnnotation(tag = "privacy_policy", annotation = privacyPolicyUrl)
                    withStyle(style = SpanStyle(color = Blue300)) {
                        append("Privacy Policy")
                    }
                    pop()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                        context.startActivity(intent)
                    },
                fontSize = 12.sp
            )

            Text(text = "v$appVersion", fontSize = 12.sp)
        }
    }
}
