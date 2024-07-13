package com.android.splitease.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.android.splitease.navigation.App
import com.android.splitease.ui.theme.SplitEaseTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(context: Context, onInitializationComplete: suspend () -> Boolean) {
    var isInitialized by remember { mutableStateOf(false) }
    var initializationFailed by remember { mutableStateOf(false) }
    var isNetworkConnected by remember { mutableStateOf(true) } // Track network connectivity

    // Function to check network connectivity
    fun checkNetworkConnectivity(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    LaunchedEffect(Unit) {
        try {// Check network connectivity when the component is launched
            isNetworkConnected = checkNetworkConnectivity()
            if (!isNetworkConnected) {
                Log.e("Network error", "MainScreen: No internet found")
                initializationFailed = true
            } else {
                // Proceed with initialization if network is connected
                isInitialized = onInitializationComplete()
                if (!isInitialized) {
                    initializationFailed = true
                }
            }
        } catch (e: Exception) {
            Log.e("Network error", "Error checking network connectivity: ${e.message}", e)
            initializationFailed = true
        }
    }

    if (initializationFailed && isNetworkConnected) {
        Toast.makeText(context, "Initialization failed. Please log in again.", Toast.LENGTH_SHORT).show()
        App(isInitialized, isNetworkConnected)
    } else if (initializationFailed && !isNetworkConnected){
        App(isInitialized, isNetworkConnected)
    } else if (isInitialized) {
        // Show the main app content
        App(isInitialized, isNetworkConnected)
    } else {
        // Show a loading indicator while initialization is in progress
        CircularProgressIndicator()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SplitEaseTheme {
        MainScreen(context = LocalContext.current, onInitializationComplete = { true })
    }
}
