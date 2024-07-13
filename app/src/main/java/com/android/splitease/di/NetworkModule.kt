package com.android.splitease.di

import com.android.splitease.services.AuthService
import com.android.splitease.services.GroupService
import com.android.splitease.services.TransactionService
import com.android.splitease.services.UserService
import com.android.splitease.utils.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.HOME_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
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
}