package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.TransactionViewModel

@Composable
fun UserDebtScreen(calculateDebt: NetworkResult<CalculateDebtResponse>) {
    val debtData = (calculateDebt as NetworkResult.Success).data
    debtData?.let { data ->
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text(text = "Creditor List", modifier = Modifier.padding(vertical = 8.dp))
            }
            items(data.creditorList) { creditor ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "Creditor: ${creditor.name} gets back ${creditor.getsBack}")
                    creditor.lentTo.forEach { lentTo ->
                        Text(text = "  Lent to ${lentTo.name}: ${lentTo.amount}")
                    }
                }
            }

            item {
                Text(text = "Debtor List", modifier = Modifier.padding(vertical = 8.dp))
            }
            items(data.debtorList) { debtor ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "Debtor: ${debtor.name}")
                    debtor.lentFrom.forEach { lentFrom ->
                        Text(text = "  Lent from ${lentFrom.name}: ${lentFrom.amount}")
                    }
                }
            }
        }
    }
}