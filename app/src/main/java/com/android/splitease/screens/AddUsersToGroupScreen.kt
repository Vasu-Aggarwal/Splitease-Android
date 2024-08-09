package com.android.splitease.screens

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Green300
import com.android.splitease.ui.theme.Green800
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUsersToGroupScreen(groupId: Int, groupViewModel: GroupViewModel = hiltViewModel(), userViewModel: UserViewModel = hiltViewModel(), navController: NavController){
    var emailSet by remember { mutableStateOf(setOf<String>()) }
    val addUsersResponse by groupViewModel.addUsersToGroup.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val isUserExists: State<NetworkResult<List<GetUserByUuidResponse>>> = userViewModel.isUserExists.collectAsState()

    // Debounced search state
    val debouncedSearchQuery by rememberDebounce(searchQuery)

    // Launch API call whenever debounced query changes
    LaunchedEffect(debouncedSearchQuery) {
        if (debouncedSearchQuery.isNotBlank()) {
            userViewModel.isUserExists(debouncedSearchQuery)
        }
    }

    LaunchedEffect(addUsersResponse) {
        if (addUsersResponse is NetworkResult.Success) {
            val message = (addUsersResponse as NetworkResult.Success).data as? String
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
            when(val result = isUserExists.value){
                is NetworkResult.Error -> {}
                is NetworkResult.Idle -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    LazyColumn {
                        item {
                            Card (
                                onClick = {
                                          navController.navigate(Screen.RegisterNewUserScreen.createRoute(
                                              if (!searchQuery.isNullOrBlank()){
                                                  searchQuery
                                              } else {
                                                  ""
                                              }
                                          ))
//                                    if (searchQuery.isNotBlank()) {
//                                        emailSet = emailSet + searchQuery
//                                        searchQuery = "" // clear the text field after adding
//                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp, 1.dp, 5.dp, 1.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ){
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
                                })
                            }
                        }
                        items(result.data!!){user ->
                            UserItem(user, searchQuery, emailSet)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: GetUserByUuidResponse, searchQuery: String, emailSet: Set<String>) {
    Card (
        onClick = {
            if (searchQuery.isNotBlank()) {
//                emailSet = emailSet + searchQuery
//                searchQuery = "" // clear the text field after adding
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp, 1.dp, 5.dp, 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ){

        Row{

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
                    text = user.name.first().toString().uppercase(), // Placeholder for the first letter of the email
                    style = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
                )

            }

            Spacer(modifier = Modifier.padding(5.dp))

            Column {
                Text(text = user.name)
                if (!user.email.isNullOrBlank()) {
                    Text(text = user.email)
                }
            }
        }
    }
}

//@Composable
//fun MarqueeText(
//    text: String,
//    modifier: Modifier = Modifier,
//    fontSize: TextUnit = TextUnit.Unspecified,
//    maxLines: Int = 1 // Typically you want a single line for marquee
//) {
//    val scrollState = rememberScrollState(0)
//
//    Box(
//        modifier = modifier
//            .horizontalScroll(scrollState)
//            .clipToBounds() // Ensure no overflow
//    ) {
//        LaunchedEffect(Unit) {
//            // Continuously scroll the text
//            while (true) {
//                scrollState.animateScrollTo(
//                    scrollState.maxValue,
//                    animationSpec = infiniteRepeatable(
//                        animation = tween(durationMillis = 5000, easing = LinearEasing),
//                        repeatMode = RepeatMode.Reverse
//                    )
//                )
//            }
//        }
//
//        Text(
//            text = text,
//            fontSize = fontSize,
//            maxLines = maxLines,
//            overflow = TextOverflow.Ellipsis, // Handle overflow gracefully
//            modifier = Modifier.fillMaxWidth() // Make the text fill the available width
//        )
//    }
//}


// Debounce helper function
@Composable
fun rememberDebounce(
    input: String,
    delayMillis: Long = 300L // Adjust delay time as needed
): State<String> {
    val state = remember { mutableStateOf(input) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(input) {
        coroutineScope.launch {
            delay(delayMillis)
            state.value = input
        }
    }

    return state
}