package com.android.splitease

import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Configuration
import androidx.work.WorkManager
import com.android.splitease.navigation.App
import com.android.splitease.repositories.AuthRepository
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.screens.MainScreen
import com.android.splitease.ui.theme.SplitEaseTheme
import com.android.splitease.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var tokenManager: TokenManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitEaseTheme {

                // Check and request notification permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                    }
                }

                // Check and request media image permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 1)
                    }
                }

                MainScreen(
                    context = LocalContext.current,
                    onInitializationComplete = { initializeApp() }
                )
            }
        }
    }

    private suspend fun initializeApp(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val accessTokenExpiryTime = tokenManager.getAuthExpiryTime()
        val refreshTokenExpiryTime = tokenManager.getRefreshExpiryTime()

        return if (currentTimeMillis > accessTokenExpiryTime) {
            if (currentTimeMillis < refreshTokenExpiryTime) {
                // Refresh token is still valid, refresh the access token
                val newAccessToken = authRepository.refreshToken()
//                tokenManager.clearTokens()
                newAccessToken != null
            } else {
                // Both tokens are expired, prompt the user to log in again
                false
            }
        } else {
            true
        }
    }

}