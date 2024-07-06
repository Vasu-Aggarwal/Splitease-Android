package com.android.splitease.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class UtilMethods {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        val date = LocalDate.parse(dateString)
        val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val dayOfMonth = date.dayOfMonth
        return "$month $dayOfMonth"
    }
}