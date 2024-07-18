package com.android.splitease.models.util

import com.android.splitease.models.util.LentDetails

data class Creditor(
    val uuid: String,
    val name: String,
    val getsBack: Double,
    val lentTo: List<LentDetails>
)
