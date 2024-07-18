package com.android.splitease.models.util

import com.android.splitease.models.util.LentDetails

data class Debtor(
    val uuid: String,
    val name: String,
    val lentFrom: List<LentDetails>
)
