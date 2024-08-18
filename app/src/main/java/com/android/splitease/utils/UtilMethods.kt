package com.android.splitease.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.text.Format
import java.text.NumberFormat
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
        val outputFormat = SimpleDateFormat("MMM\ndd", Locale.getDefault())

        // Parse the original date
        val date: Date = inputFormat.parse(originalDate) ?: return ""

        // Format the date into the desired output format
        return outputFormat.format(date)
    }

    fun formatAmount(amount: Double): String{
        return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)
    }

    fun abbreviateName(fullName: String): String {
        // Split the full name into parts
        val nameParts = fullName.split(" ")
        // If there's only one part, return it as it is
        if (nameParts.size == 1) return fullName
        // Get the first name
        val firstName = nameParts.first()
        // Get the initial of the last name
        val lastNameInitial = nameParts.last().firstOrNull()?.toString() ?: ""
        // Combine them into the abbreviated form
        return "$firstName $lastNameInitial."
    }
}