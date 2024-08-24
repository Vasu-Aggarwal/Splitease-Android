package com.android.splitease.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.White
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUsersToGroupScreen(groupId: Int, groupViewModel: GroupViewModel = hiltViewModel(), userViewModel: UserViewModel = hiltViewModel(), navController: NavController){
    var emailSet by remember { mutableStateOf(setOf<String>()) }
    val addUsersResponse by groupViewModel.addUsersToGroup.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val isUserExists: State<NetworkResult<List<GetUserByUuidResponse>>> = userViewModel.isUserExists.collectAsState()
    val groupMembers: State<NetworkResult<List<GetGroupMembersV2Response>>> = groupViewModel.groupMembersV2.collectAsState()

    // Retrieve the selected user's name and UUID from the savedStateHandle
    val registeredUser = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("registeredUser", "")?.collectAsState()

    // Add registeredUser to emailSet if it's not null or blank
    LaunchedEffect(registeredUser?.value) {
        registeredUser?.value?.let { userEmail ->
            if (userEmail.isNotBlank()) {
                emailSet = emailSet + userEmail
                searchQuery = "" // clear the search query
            }
        }
    }

    // Debounced search state
    val debouncedSearchQuery by rememberDebounce(searchQuery)

    // Launch API call whenever debounced query changes
    LaunchedEffect(debouncedSearchQuery) {
        if (debouncedSearchQuery.isNotBlank()) {
            userViewModel.isUserExists(debouncedSearchQuery)
        }
    }

    LaunchedEffect(groupId) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    LaunchedEffect(addUsersResponse) {
        if (addUsersResponse is NetworkResult.Success) {
            val message = (addUsersResponse as NetworkResult.Success).data!!.message as? String
            if (message == "Users added successfully") {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Enter name, or email", fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(8.dp)
                            ),
                        singleLine = true,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.background, focusedContainerColor = MaterialTheme.colorScheme.background)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Here you can make the API request using emailSet
                val addUsersToGroupRequest = AddUsersToGroupRequest(groupId, emailSet)
                groupViewModel.addUsersToGroup(addUsersToGroupRequest)
            }) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Submit")
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(emailSet.toList()) { addedEmail ->
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        // Circular image placeholder
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(50)
                                )
                                .size(50.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                text = addedEmail.first().toString().uppercase(), // Placeholder for the first letter of the email
                                style = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
                            )
                            // Delete Icon
                            IconButton(
                                onClick = { emailSet = emailSet - addedEmail },
                                modifier = Modifier
                                    .align(androidx.compose.ui.Alignment.TopEnd)
                                    .background(
                                        MaterialTheme.colorScheme.onPrimary,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Delete User",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Email text below the image
                        Text(
                            text = addedEmail,
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
            Card (
                onClick = {
                    navController.navigate(Screen.RegisterNewUserScreen.createRoute(
                        if (!searchQuery.isNullOrBlank()){
                            searchQuery
                        } else {
                            " "
                        }
                    ))
                },
                modifier = Modifier
                    .padding(5.dp, 1.dp, 5.dp, 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                            .size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add person")
                    }

                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(text = buildAnnotatedString {
                        append("Add ")
                        if (searchQuery.isEmpty()){
                            append("a new contact")
                        } else{
                            withStyle(style = SpanStyle(color = AppConstants.LENT_COLOR)){
                                append("\"$searchQuery\"")
                            }
                        }
                        append(" to SplitEase")
                    }, textAlign = TextAlign.Center)
                }
            }
            when(val result = isUserExists.value){
                is NetworkResult.Error -> {}
                is NetworkResult.Idle -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {

                    // Extract group members' emails or mobiles
                    val groupEmails = groupMembers.value.data?.mapNotNull { it.email } ?: emptyList()
//                    val groupMobiles = groupMembers.value.data?.mapNotNull { it.mobile } ?: emptyList()

                    LazyColumn {
                        items(result.data!!){user ->
                            val isAlreadyInGroup = groupEmails.contains(user.email) /*|| groupMobiles.contains(user.mobile)*/
                            UserItem(user, emailSet, isAlreadyInGroup){selectedUser ->
                                if (!user.email.isNullOrBlank()){
                                    emailSet = emailSet + user.email
                                } else {
                                    emailSet = emailSet + user.name
                                }
                                searchQuery = "" // clear the text field after adding
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: GetUserByUuidResponse,
    emailSet: Set<String>,
    isAlreadyInGroup: Boolean,
    onUserSelected: (String) -> Unit
) {

    val isUserAddedEmail = emailSet.contains(user.email)
    val isUserAddedMobile = emailSet.contains(user.mobile)

    Card (
        onClick = {
            if (!isUserAddedEmail || !isUserAddedMobile) {
                if (!user.email.isNullOrBlank()){
                    onUserSelected(user.email) // Add the user's email to the selected list
                } else {
                    onUserSelected(user.mobile) // Add the user's mobile to the selected list
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp, 1.dp, 5.dp, 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        enabled = !isAlreadyInGroup
    ){

        Row{
            FlippingCard(isUserAdded = !isUserAddedEmail, userName = user.name.first().toString().uppercase())

            Spacer(modifier = Modifier.padding(5.dp))

            Column {
                Text(text = user.name, color = if (isUserAddedEmail) Color.Gray else if (isAlreadyInGroup) Color.Gray else White) // Change color if user is added)
                if (!user.email.isNullOrBlank()) {
                    Text(text = if (isAlreadyInGroup) "already in group" else user.email, color = if (isUserAddedEmail) Color.Gray else if (isAlreadyInGroup) Color.Gray else White)
                }
            }
        }
    }
}

// Debounce helper function
@Composable
fun rememberDebounce(
    input: String,
    delayMillis: Long = 500L // Adjust delay time as needed
): State<String> {
    val debouncedValue = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(input) {
        debounceJob?.cancel() // Cancel the previous job if it exists
        debounceJob = coroutineScope.launch {
            delay(delayMillis)
            debouncedValue.value = input // Update the debounced value after the delay
        }
    }

    return debouncedValue
}

@Composable
fun FlippingCard(isUserAdded: Boolean, userName: String) {
    // Rotation animation state
    val rotationState by animateFloatAsState(
        targetValue = if (isUserAdded) 180f else 0f, // Flip 180 degrees
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 500) // Adjust duration as needed
    )

    // Flip animation
    Box(
        modifier = Modifier
            .size(50.dp)
            .graphicsLayer(
                rotationY = rotationState, // Rotate around the Y-axis
                cameraDistance = 8f // Adjust camera distance for better 3D effect
            )
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.graphicsLayer(
                alpha = if (rotationState < 90f) 1f else 0f // Fade out the front side as it flips
            )
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "User Added",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Box(
            modifier = Modifier.graphicsLayer(
                alpha = if (rotationState >= 90f) 1f else 0f, // Fade in the back side as it flips
                rotationY = 180f
            )
        ) {
            Text(
                text = userName.firstOrNull()?.toString()?.uppercase() ?: "",
                style = TextStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp)
            )
        }
    }
}