package com.android.splitease.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.splitease.models.responses.GetTransactionByIdResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Grey300
import com.android.splitease.ui.theme.Grey400
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.LoadingOverlay
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedTransactionScreen(transactionId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), navController: NavController) {

    val transaction: State<NetworkResult<GetTransactionByIdResponse>> = transactionViewModel.getTransaction.collectAsState()
    val restore: State<NetworkResult<GetTransactionByIdResponse>> = transactionViewModel.restoreTransaction.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val userUuid = tokenManager.getUserUuid()

    var loading by remember { mutableStateOf(false) }


    LaunchedEffect(transactionId) {
        transactionViewModel.getTransactionById(transactionId)
        loading = true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Box(
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .offset(x = 60.dp, y = 65.dp)
                        .zIndex(1f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = transaction.value.data?.category?.imageUrl)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        ),
                        contentDescription = "Category Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(50))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }

                TopAppBar(
                    colors =
                            if (transaction.value.data?.status != 0)
                                TopAppBarColors(containerColor = Color.White, scrolledContainerColor = Color.White, navigationIconContentColor = Color.Black, titleContentColor = Color.White, actionIconContentColor = Color.Black)
                            else
                                TopAppBarColors(containerColor = Grey400, scrolledContainerColor = Grey400, navigationIconContentColor = Color.Black, titleContentColor = Color.White, actionIconContentColor = Color.Black),
                    title = {
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (transaction.value.data?.status == 0){
                            IconButton(onClick = {
                                loading = true
                                transactionViewModel.restoreTransaction(transactionId)
                                navController.popBackStack()
                            }) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = "Delete")
                            }
                        } else {
                            IconButton(onClick = {
                                loading = true
                                transactionViewModel.deleteTransaction(transactionId)
                                navController.popBackStack()
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            }

                            IconButton(onClick = {  }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Delete")
                            }
                        }
                    }
                )
            }
        }
    ){ padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {

            when (restore.value) {
                is NetworkResult.Loading -> { loading = true }
                is NetworkResult.Success, is NetworkResult.Error, is NetworkResult.Idle -> { loading = false }
            }

            when(val result = transaction.value){
                is NetworkResult.Error -> {loading = false}
                is NetworkResult.Idle -> {loading = false}
                is NetworkResult.Loading -> {loading = true}
                is NetworkResult.Success -> {
                    loading = false
                    result.data?.let { transactionData ->
                        Column(
                            modifier = Modifier.padding(start = 60.dp, top = 30.dp)
                        ) {
                            if (transactionData.status == 0){ //means deleted
                                Text(text = transactionData.description, fontSize = 25.sp, textDecoration = TextDecoration.LineThrough)
                                Text(text = UtilMethods.formatAmount(transactionData.amount), fontSize = 25.sp, textDecoration = TextDecoration.LineThrough)
                            }
                            else {
                                Text(text = transactionData.description, fontSize = 25.sp)
                                Text(text = UtilMethods.formatAmount(transactionData.amount), fontSize = 25.sp)
                            }
                            Text(text = "Added on ${transactionData.createdOn}", fontSize = 13.sp, modifier = Modifier.padding(top = 15.dp), color = Grey400)
                            Text(text = buildAnnotatedString {
                                if(transaction.value.data?.status == 0) {
                                    withStyle(style = SpanStyle(color = AppConstants.OWE_COLOR)){
                                        append("Deleted on ${transactionData.modifiedOn}")
                                    }
                                }
                                else if (transactionData.modifiedOn != transactionData.createdOn){
                                    append("Modified on ${transactionData.modifiedOn}")
                                } else {
                                    append("")
                                }
                            }, fontSize = 13.sp, color = Grey400)

                            Text(text = buildAnnotatedString {
                                if(transactionData.userUuid == userUuid)
                                    append("You paid ")
                                else
                                    append("${UtilMethods.abbreviateName(transactionData.payerName)} paid ")

                                append(UtilMethods.formatAmount(transactionData.amount))
                            }, modifier = Modifier.padding(top = 15.dp, bottom = 5.dp))

                            transactionData.userLedgerDetails.forEach { userLedgerDetails ->
                                Text(text = buildAnnotatedString {
                                    append(UtilMethods.abbreviateName(userLedgerDetails.name))
                                    if (userLedgerDetails.owedOrLent.equals("OWED", ignoreCase = true))
                                        append(" owes ")
                                    else
                                        append(" paid ")
                                    append(UtilMethods.formatAmount(userLedgerDetails.amount))
                                }, color = Grey400, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
        if (loading){
            LoadingOverlay()
        }
    }
}

@Composable
fun LShapedLine(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.dp.toPx()
        val horizontalLength = 30.dp.toPx() // Length of the horizontal part
        val verticalLength = 30.dp.toPx() // Length of the vertical part

        // Draw the vertical line first, starting from the top left
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, verticalLength),
            strokeWidth = strokeWidth
        )

        // Draw the horizontal line, starting from the end of the vertical line
        drawLine(
            color = Color.Gray,
            start = Offset(0f, verticalLength),
            end = Offset(horizontalLength, verticalLength),
            strokeWidth = strokeWidth
        )
    }
}