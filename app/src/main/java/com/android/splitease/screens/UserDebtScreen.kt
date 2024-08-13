package com.android.splitease.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.util.Creditor
import com.android.splitease.models.util.Debtor
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDebtScreen(
    groupId: Int,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    navController: NavController
) {

    LaunchedEffect(groupId) {
        transactionViewModel.calculateDebt(groupId)
    }

    val calculateDebt by transactionViewModel.calculateDebt.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Balances") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ){ padding ->
        when (calculateDebt) {
            is NetworkResult.Error -> Text(text = "error")
            is NetworkResult.Idle -> Text(text = "idle")
            is NetworkResult.Loading -> Text(text = "loading")
            is NetworkResult.Success -> {
                val debtData = (calculateDebt as NetworkResult.Success).data
                debtData?.let { data ->
                    if (debtData.debtorList.isEmpty() && debtData.creditorList.isEmpty()){
                        Box(
                            modifier = Modifier.padding(padding).fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ){
                            Text(text = "Everything Settled up")
                        }
                    } else {
                        LazyColumn(modifier = Modifier
                            .padding(16.dp)
                            .padding(padding)) {
                            items(data.creditorList) { creditor ->
                                CreditorItem(creditor, navController, groupId)
                            }

                            items(data.debtorList) { debtor ->
                                DebtorItem(debtor, navController, groupId)
                            }
                        }   
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
fun CreditorItem(creditor: Creditor, navController: NavController, groupId: Int) {
    ExpandableItem(
        header = {
            Text(
                text = buildAnnotatedString {
                    append("${creditor.name} gets back ")
                    withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                        append(UtilMethods.formatAmount(creditor.getsBack))
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        },
        expandedContent = {
            creditor.lentTo.forEach { lentTo ->
                Text(
                    text = buildAnnotatedString {
                        append("${lentTo.name} owes ")
                        withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                            append(UtilMethods.formatAmount(lentTo.amount))
                        }
                        append(" to ${creditor.name}")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.SettleUpPayerScreen.createRoute(groupId))
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Settle Up")
                    }
                }
            }
        }
    )
}

@Composable
fun DebtorItem(debtor: Debtor, navController: NavController, groupId: Int) {
    ExpandableItem(
        header = {
            Text(
                text = buildAnnotatedString {
                    append("${debtor.name} owes ")
                    withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                        append(UtilMethods.formatAmount(debtor.totalReturnAmount))
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        },
        expandedContent = {
            debtor.lentFrom.forEach { lentFrom ->
                Text(
                    text = buildAnnotatedString {
                        append("${debtor.name} owes ")
                        withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                            append(UtilMethods.formatAmount(lentFrom.amount))
                        }
                        append(" to ${lentFrom.name}")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.SettleUpPayerScreen.createRoute(groupId))
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Settle Up")
                    }
                }
            }
        }
    )
}

