package com.android.splitease.utils

import android.content.SharedPreferences

class TokenManager (private val sharedPreferences: SharedPreferences) {

    fun saveAuthToken(authToken: String?, refreshToken: String?, userUuid: String?) {
        with(sharedPreferences.edit()) {
            putString("auth_token", authToken)
            putString("refresh_token", refreshToken)
            putString("user_uuid", userUuid)
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

    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove("auth_token")
            remove("refresh_token")
            apply()
        }
    }
}