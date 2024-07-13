package com.android.splitease.di

import android.util.Log
import com.android.splitease.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkExceptionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: java.net.ConnectException) {
            Log.e("NetworkException", "intercept: ${e.message}")
            throw NetworkException("Failed to connect to server. Please check your internet connection.")
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("NetworkException", "intercept: ${e.message}")
            throw NetworkException("Connection timed out. Please try again later.")
        } catch (e: IOException) {
            Log.e("NetworkException", "intercept: ${e.message}")
            throw NetworkException("An unexpected error occurred: ${e.message}")
        } catch (e: Exception){
            Log.e("NetworkException", "intercept: ${e.message}")
            throw NetworkException(AppConstants.UNEXPECTED_ERROR)
        }
    }
}

class NetworkException(message: String) : IOException(message)