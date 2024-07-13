package com.android.splitease.utils

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast

class TokenManager (private val sharedPreferences: SharedPreferences) {
    private val oneMonthInMillis = 30 * 24 * 60 * 60 * 1000L // Approx. 30 days
    private val fiveHourInMillis = 5*60*60*1000L // Approx. 5 hours

    fun saveAuthToken(authToken: String?, refreshToken: String?, userUuid: String?) {
        with(sharedPreferences.edit()) {
            val currentTimeMillis = System.currentTimeMillis()
            putString("auth_token", authToken)
            putString("refresh_token", refreshToken)
            putString("user_uuid", userUuid)
            putLong("auth_token_saved_time", currentTimeMillis)
            putLong("auth_token_expiry_time", currentTimeMillis + fiveHourInMillis)
            putLong("refresh_token_saved_time", currentTimeMillis)
            putLong("refresh_token_expiry_time", currentTimeMillis + oneMonthInMillis)
            apply()
            Log.d("Update JWT", "Tokens saved successfully")
        }
    }

    fun updateAuthToken(authToken: String?) {
        with(sharedPreferences.edit()) {
            val currentTimeMillis = System.currentTimeMillis()
            putString("auth_token", authToken)
            putLong("auth_token_saved_time", currentTimeMillis)
            putLong("auth_token_expiry_time", currentTimeMillis + fiveHourInMillis)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun getUserUuid(): String? {
        return sharedPreferences.getString("user_uuid", null)
    }

    fun getAuthExpiryTime(): Long {
        return sharedPreferences.getLong("auth_token_expiry_time", 0)
    }

    fun getRefreshExpiryTime(): Long {
        return sharedPreferences.getLong("refresh_token_expiry_time", 0)
    }

    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove("auth_token")
            remove("auth_token_expiry_time")
            remove("auth_token_saved_time")
            apply()
        }
    }
}