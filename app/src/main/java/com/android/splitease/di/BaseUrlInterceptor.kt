package com.android.splitease.di

import android.content.SharedPreferences
import com.android.splitease.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = sharedPreferences.getString(AppConstants.AWS_BASE_URL, AppConstants.AWS_BASE_URL)
        val newRequest = originalRequest.newBuilder()
            .url(newUrl + originalRequest.url.encodedPath)
            .build()

        return chain.proceed(newRequest)
    }
}
