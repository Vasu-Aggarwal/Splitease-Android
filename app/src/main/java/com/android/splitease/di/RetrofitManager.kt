package com.android.splitease.di

import android.content.SharedPreferences
import android.util.Log
import com.android.splitease.services.AuthService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.UrlProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
class RetrofitManager @Inject constructor(private val sharedPreferences: SharedPreferences, private val okHttpClient: OkHttpClient) {

    @Volatile
    private var retrofitInstance: Retrofit? = null

    fun getRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            retrofitInstance ?: buildRetrofit().also { retrofitInstance = it }
        }
    }

    private fun buildRetrofit(): Retrofit {
        val baseUrl = sharedPreferences.getString("AWS_BASE_URL", AppConstants.AWS_BASE_URL)

        Log.d("RetrofitManager", "Using base URL: $baseUrl")

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun refreshRetrofit() {
        synchronized(this) {
            retrofitInstance = buildRetrofit() // Rebuild Retrofit with the new URL
        }
    }
}
