package com.android.splitease.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.splitease.models.responses.CalculateDebtResponse
import com.android.splitease.models.responses.GetTransactionsByGroupResponse
import com.android.splitease.models.util.Creditor
import com.android.splitease.models.util.Debtor
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.TransactionViewModel

@Composable
fun UserDebtScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel()) {
    LaunchedEffect(groupId) {
        transactionViewModel.calculateDebt(groupId)
    }
    val calculateDebt by transactionViewModel.calculateDebt.collectAsState()
    when(calculateDebt) {
        is NetworkResult.Error -> Text(text = "error")
        is NetworkResult.Idle -> Text(text = "idle")
        is NetworkResult.Loading -> Text(text = "loading")
        is NetworkResult.Success -> {
            val debtData = (calculateDebt as NetworkResult.Success).data
            debtData?.let { data ->
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(data.creditorList) { creditor ->
                        CreditorItem(creditor)
                    }

                    items(data.debtorList) { debtor ->
                        DebtorItem(debtor)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableItem(
    header: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp)
        ) {
            header()
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
        if (expanded) {
            expandedContent()
        }
    }
}

@Composable
fun CreditorItem(creditor: Creditor) {
    ExpandableItem(
        header = {
            Text(
                text = "${creditor.name} gets back ₹${String.format("%.2f", creditor.getsBack)}",
                style = MaterialTheme.typography.titleMedium
            )
        },
        expandedContent = {
            creditor.lentTo.forEach { lentTo ->
                Text(
                    text = "${lentTo.name} pays ₹${String.format("%.2f", lentTo.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    )
}

@Composable
fun DebtorItem(debtor: Debtor) {
    ExpandableItem(
        header = {
            Text(
                text = "${debtor.name} owes",
                style = MaterialTheme.typography.titleMedium
            )
        },
        expandedContent = {
            debtor.lentFrom.forEach { lentFrom ->
                Text(
                    text = "${debtor.name} owes to ${lentFrom.name} ₹${String.format("%.2f", lentFrom.amount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    )
}

