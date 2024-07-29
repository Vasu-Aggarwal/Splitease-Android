package com.android.splitease.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

object UtilMethods {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(originalDate: String): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        // Parse the original date
        val date: Date = inputFormat.parse(originalDate) ?: return ""

        // Format the date into the desired output format
        return outputFormat.format(date)
    }

    fun formatAmount(amount: Double): String{
        return AppConstants.RUPEE+String.format("%.2f", amount)
    }
}