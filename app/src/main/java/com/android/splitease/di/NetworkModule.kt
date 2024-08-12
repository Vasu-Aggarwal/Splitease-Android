package com.android.splitease.di

import com.android.splitease.services.AuthService
import com.android.splitease.services.CategoryService
import com.android.splitease.services.GroupService
import com.android.splitease.services.TransactionService
import com.android.splitease.services.UserService
import com.android.splitease.utils.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.OFFICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroupService(retrofit: Retrofit): GroupService {
        return retrofit.create(GroupService::class.java)
    }

    @Provides
    @Singleton
    fun provideTransactionService(retrofit: Retrofit): TransactionService {
        return retrofit.create(TransactionService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService{
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryService(retrofit: Retrofit): CategoryService{
        return retrofit.create(CategoryService::class.java)
    }
}