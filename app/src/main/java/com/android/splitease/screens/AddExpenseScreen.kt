package com.android.splitease.screens

import android.content.Context
import android.graphics.Paint.Align
import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.splitease.R
import com.android.splitease.models.requests.AddTransactionRequest
import com.android.splitease.models.responses.AddTransactionResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Grey800
import com.android.splitease.ui.theme.White
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.SplitBy
import com.android.splitease.utils.TokenManager
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.TransactionViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(groupId: Int, transactionViewModel: TransactionViewModel = hiltViewModel(), groupViewModel: GroupViewModel = hiltViewModel(), navController: NavController){
    val addTransaction: State<NetworkResult<AddTransactionResponse>> = transactionViewModel.addTransaction.collectAsState()
    var message by remember { mutableStateOf("") }
    val groupMembers by groupViewModel.groupMembersV2.collectAsState()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val userUuid = tokenManager.getUserUuid()

    // Retrieve saved description and amount
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    var description by remember { mutableStateOf(savedStateHandle?.get<String>("description") ?: "") }
    var amount by remember { mutableDoubleStateOf(savedStateHandle?.get<Double>("amount") ?: 0.0) }

    // Retrieve the selected user's name and UUID from the savedStateHandle
    val selectedUserName = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selectedUserName", "You")?.collectAsState()
    val selectedUserUuid = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selectedUserUuid", "")?.collectAsState()

    var contributions by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    val contributionsState = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selectedData", emptyMap<String, Double>())?.collectAsState()

    // Update contributions whenever contri changes
    LaunchedEffect(contributionsState?.value) {
        if (contributionsState != null && contributionsState.value.isNotEmpty()) {
            contributions = contributionsState.value
        }
    }

    contributions.forEach { _ ->
        if (groupMembers is NetworkResult.Success) {
            val members = groupMembers.data
            var payingEmail: String = ""

            // Find the paying user’s email
            members?.forEach { member ->
                if (member.userUuid == selectedUserUuid?.value) {
                    payingEmail = member.email
                }
            }

            // If the paying user's email is found, ensure it's present in the contributions map
            payingEmail.let { email ->
                // Create a new map with the updated entry
                val updatedContributions = contributions.toMutableMap().apply {
                    if (!containsKey(email)) {
                        this[email] = 0.0
                    }
                }
                // Update the state with the new map
                contributions = updatedContributions
            }
        }
    }

    // Update savedStateHandle whenever description or amount changes
    LaunchedEffect(description) {
        savedStateHandle?.set("description", description)
    }

    LaunchedEffect(amount) {
        savedStateHandle?.set("amount", amount)
    }

    // Fetch group members when the groupId changes
    LaunchedEffect(groupId) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(){
                        Text(
                            text = "Add Expense",
                            style = MaterialTheme.typography.titleLarge, // Adjust text style if needed
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if(contributions.isEmpty() && description.isNotBlank() && amount>0.00){
                            if (groupMembers is NetworkResult.Success) {
                                val members = (groupMembers as NetworkResult.Success).data
                                if (!members.isNullOrEmpty()) {
                                    val equalAmount = amount / members.size
                                    val contributionsEqual = mutableMapOf<String, Double>()
                                    // Distribute the amount equally
                                    members.forEachIndexed { index, member ->
                                        contributionsEqual[member.email] = if (index == members.size - 1) {
                                            // Adjust the last member's contribution to ensure the total is correct
                                            amount - contributionsEqual.values.sum()
                                        } else {
                                            equalAmount
                                        }
                                    }

                                    val addTransactionRequest = AddTransactionRequest(
                                        amount = amount,
                                        splitBy = SplitBy.EQUAL,
                                        group = groupId,
                                        userUuid = if (selectedUserName!!.value.equals("You", ignoreCase = true)) userUuid!! else selectedUserUuid!!.value,
                                        description = description,
                                        category = "Adventure",
                                        usersInvolved = contributionsEqual
                                    )
                                    val gson = Gson()
                                    val json = gson.toJson(addTransactionRequest)
                                    Log.d("AES", "AddExpenseScreen: $json")
                                    transactionViewModel.addTransaction(addTransactionRequest)
                                }
                            }
                        } else if (description.isNotBlank() && amount>0.00 && contributions.isNotEmpty()) {
                            val addTransactionRequest = AddTransactionRequest(
                                amount = amount,
                                splitBy = SplitBy.EQUAL,
                                group = groupId,
                                userUuid = selectedUserUuid!!.value,
                                description = description,
                                category = "Entertainment",
                                usersInvolved = contributions
                            )
                            val gson = Gson()
                            val json = gson.toJson(addTransactionRequest)
                            Log.d("AES", "AddExpenseScreen: $json")
                            transactionViewModel.addTransaction(addTransactionRequest)
                        } else {
                            message = "Please enter a valid description and amount"
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .fillMaxHeight(0.14f)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate(Screen.CategoryScreen.route) }, modifier = Modifier
                    .background(Grey800, RoundedCornerShape(8.dp))
                    .border(1.dp, color = White, RoundedCornerShape(8.dp))
                    .align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "category")
                }

                Spacer(modifier = Modifier.width(15.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Enter a description") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.CenterVertically),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.background, focusedContainerColor = MaterialTheme.colorScheme.background)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { /*TODO*/ }, modifier = Modifier
                    .background(Grey800, RoundedCornerShape(8.dp))
                    .border(1.dp, color = White, RoundedCornerShape(8.dp))
                ) {
                    Icon(painter = painterResource(id = R.drawable.rupee_indian), contentDescription = "category")
                }

                Spacer(modifier = Modifier.width(15.dp))

                TextField(
                    value = if (amount == 0.0) "" else amount.toString(),
                    onValueChange = { it.toDoubleOrNull()?.let { amt -> amount = amt } },
                    label = { Text("0.00") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.Top),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.background, focusedContainerColor = MaterialTheme.colorScheme.background)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(text = message)
            }
            when (val result = addTransaction.value) {
                is NetworkResult.Success -> {
                    message = "Transaction added successfully"
                    navController.popBackStack(Screen.DetailedGroupScreen.createRoute(groupId), false)
                }
                is NetworkResult.Error -> {
                    message = result.message ?: "An error occurred"
                }
                is NetworkResult.Loading -> {
                    message = "Adding transaction..."
                }

                is NetworkResult.Idle -> message = "Idle"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Paid by", modifier = Modifier.padding(end = 8.dp))
                    Button(
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(5.dp)
                            .border(1.dp, shape = RectangleShape, color = Color.White),
                        shape = RoundedCornerShape(15),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        onClick = { navController.navigate(Screen.SelectPayingUserScreen.createRoute(groupId)) }
                    ) {
                        Text(text = if(userUuid == selectedUserUuid!!.value) "You" else selectedUserName!!.value, maxLines = 1)
                    }
                }

                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "and split", modifier = Modifier.padding(end = 8.dp))
                    Button(
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(5.dp)
                            .border(1.dp, shape = RectangleShape, color = Color.White),
                        shape = RoundedCornerShape(15),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        onClick = { navController.navigate(Screen.SplitMethodScreen.createRoute(groupId, amount)) }
                    ) {
                        Text(text = "Equally", maxLines = 1)
                    }
                }
            }
        }
    }
}