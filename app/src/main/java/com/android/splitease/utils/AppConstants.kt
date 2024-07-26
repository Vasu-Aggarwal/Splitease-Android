package com.android.splitease.utils

import java.util.concurrent.TimeUnit

object AppConstants {
    const val OFFICE_BASE_URL = "http://10.100.100.210:9090"
    const val HOME_BASE_URL = "http://192.168.1.24:9090"
    const val TRANSACTION_URL = "/api/transaction"
    const val AUTH_URL = "/auth"
    const val GROUP_URL = "/api/group"
    const val USER_URL = "/api/user"
    const val CACHE_TTL = 60*1000 //1 minute
    const val UNEXPECTED_ERROR = "Something went wrong. Please try again later !!"
}