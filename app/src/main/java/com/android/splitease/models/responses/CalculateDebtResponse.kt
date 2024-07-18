package com.android.splitease.models.responses

import com.android.splitease.models.util.Creditor
import com.android.splitease.models.util.Debtor

data class CalculateDebtResponse(
    val creditorList: List<Creditor>,
    val debtorList: List<Debtor>
)
