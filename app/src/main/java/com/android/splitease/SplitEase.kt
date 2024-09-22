package com.android.splitease

import android.app.Application
import android.util.Log
import com.android.splitease.services.S3Helper
import com.android.splitease.utils.AppConstants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SplitEase : Application(){
    override fun onCreate() {
        super.onCreate()
        // Initialize AWS client
        val s3Helper = S3Helper(this@SplitEase) // Pass the context to the S3Helper
        s3Helper.fetchIpFromS3("myconfigbucketone", "config.txt") { ipAddress ->
            if (ipAddress != null) {
                println("Fetched IP Address: $ipAddress")
            } else {
                // Handle the error
                println("Failed to fetch IP Address")
            }
            AppConstants.AWS_BASE_URL = "http://$ipAddress:9090"
            Log.d("AWS S3 IP: ", "AWS URL: ${AppConstants.AWS_BASE_URL}")
            // Save the URL to SharedPreferences
            val sharedPreferences = getSharedPreferences("secure_prefs", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("AWS_BASE_URL", AppConstants.AWS_BASE_URL)
                apply()
            }
        }
    }
}