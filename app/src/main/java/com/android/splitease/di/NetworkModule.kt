package com.android.splitease.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.splitease.services.AuthService
import com.android.splitease.services.CategoryService
import com.android.splitease.services.GroupService
import com.android.splitease.services.TransactionService
import com.android.splitease.services.UserService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.UrlProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(NetworkExceptionInterceptor())
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitManager(
        sharedPreferences: SharedPreferences,
        okHttpClient: OkHttpClient
    ): RetrofitManager {
        Log.d("RetrofitManager", "Reinitialized")
        return RetrofitManager(sharedPreferences, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofitManager: RetrofitManager): AuthService {
        return retrofitManager.getRetrofit().create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroupService(retrofitManager: RetrofitManager): GroupService {
        return retrofitManager.getRetrofit().create(GroupService::class.java)
    }

    @Provides
    @Singleton
    fun provideTransactionService(retrofitManager: RetrofitManager): TransactionService {
        return retrofitManager.getRetrofit().create(TransactionService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofitManager: RetrofitManager): UserService {
        return retrofitManager.getRetrofit().create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryService(retrofitManager: RetrofitManager): CategoryService {
        return retrofitManager.getRetrofit().create(CategoryService::class.java)
    }
}
