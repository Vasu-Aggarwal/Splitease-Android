package com.android.splitease.di

import android.content.Context
import android.content.SharedPreferences
import com.android.splitease.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideTokenManager(sharedPreferences: SharedPreferences): TokenManager {
        return TokenManager(sharedPreferences)
    }
}