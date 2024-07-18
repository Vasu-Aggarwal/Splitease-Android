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
fun UserDebtScreen(transactionViewModel: TransactionViewModel = hiltViewModel()) {
    val calculateDebt: State<NetworkResult<CalculateDebtResponse>> = transactionViewModel.calculateDebt.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(text = "Creditor List", modifier = Modifier.padding(vertical = 8.dp))
        }
        items(calculateDebt.value.data!!.creditorList){ creditor ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = "Creditor: ${creditor.uuid} gets back ${creditor.getsBack}")
                creditor.lentTo.forEach { lentTo ->
                    Text(text = "  Lent to ${lentTo.uuid}: ${lentTo.amount}")
                }
            }
        }

        item {
            Text(text = "Debtor List", modifier = Modifier.padding(vertical = 8.dp))
        }
        items(calculateDebt.value.data!!.debtorList) { debtor ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = "Debtor: ${debtor.uuid}")
                debtor.lentFrom.forEach { lentFrom ->
                    Text(text = "  Lent from ${lentFrom.uuid}: ${lentFrom.amount}")
                }
            }
        }
    }
}