package com.android.splitease.services

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import java.io.BufferedReader
import java.io.InputStreamReader

class S3Helper(context: Context) {

    private val credentialsProvider = CognitoCachingCredentialsProvider(
        context,
        "us-east-1:df48231a-f2b9-40ae-b923-3d0fdeb0135f",
//        "us-east-1:d27e176c-5581-4b38-8fad-c3b729c0e2d9", // Replace with your Cognito Identity Pool ID
        Regions.US_EAST_1 // Replace with your AWS region
    )

    private val s3Client = AmazonS3Client(credentialsProvider)

    fun fetchIpFromS3(bucketName: String, fileKey: String, callback: (String?) -> Unit) {
        Thread {
            try {
                val s3Object = s3Client.getObject(GetObjectRequest(bucketName, fileKey))
//                Log.d("AWS S3 IP: ", "initeApp: $s3Object")
                val inputStream = s3Object.objectContent
                val reader = BufferedReader(InputStreamReader(inputStream))
                val ipAddress = reader.readLine() // Read the IP address from the file
//                Log.d("AWS S3 IP: ", "App: $ipAddress")
                reader.close()
                callback(ipAddress) // Return the IP address through the callback
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null) // Return null in case of error
            }
        }.start()
    }

}