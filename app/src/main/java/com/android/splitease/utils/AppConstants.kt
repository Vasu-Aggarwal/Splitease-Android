package com.android.splitease.utils

import com.android.splitease.ui.theme.DeepOrange400
import com.android.splitease.ui.theme.Green300
import java.util.concurrent.TimeUnit

object AppConstants {
    const val OFFICE_BASE_URL = "http://10.100.101.30:9090"
    const val HOME_BASE_URL = "http://192.168.1.24:9090"
    const val TRANSACTION_URL = "/api/transaction"
    const val AUTH_URL = "/auth"
    const val GROUP_URL = "/api/group"
    const val USER_URL = "/api/user"
    const val CATEGORY_URL = "/api/category"
    const val CACHE_TTL = 60*1000 //1 minute
    const val UNEXPECTED_ERROR = "Something went wrong. Please try again later !!"
    const val RUPEE = "₹"
    val OWE_COLOR = DeepOrange400
    val LENT_COLOR = Green300
    const val ALL_GROUPS = "allGroups"
    const val GROUPS_THAT_OWE_YOU = "groupsThatOweYou"
    const val GROUPS_YOU_OWE = "groupsYouOwe"
    const val OUTSTANDING_BALANCE = "outstandingBalance"
}