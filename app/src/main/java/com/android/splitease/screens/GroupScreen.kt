package com.android.splitease.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.GetOverallUserBalance
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.android.splitease.viewmodels.UserViewModel
import kotlin.math.abs

@Composable
fun GroupScreen(viewModel: GroupViewModel = hiltViewModel(), navController: NavController, userViewModel: UserViewModel = hiltViewModel()) {
    val groups: State<NetworkResult<List<AddGroupResponse>>> = viewModel.groups.collectAsState()
    val userBalance: State<NetworkResult<GetOverallUserBalance>> = userViewModel.userBalance.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userViewModel.getOverallUserBalance()
    }
    Column{
        when (val result = userBalance.value) {
            is NetworkResult.Success -> {
                val balance = result.data!!.netBalance
                Text(
                    text = when {
                        balance < 0 -> "Overall, you are owed Rs.${abs(balance)}"
                        balance > 0 -> "Overall, you owe Rs.${abs(balance)}"
                        else -> ""
                    }
                )
            }

            is NetworkResult.Error -> {
                Text(text = "Error loading balance")
            }

            is NetworkResult.Loading -> {
                Text(text = "")
            }

            is NetworkResult.Idle -> {
                // Do nothing or show some idle state
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) { // Adjust padding as needed
            groups.value.data?.let { groupList ->
                items(groupList) { group ->
                    GroupItem(group = group, viewModel, navController)
                }
            }
            item {
                StartNewGroup(navController)
            }
        }
    }
}

@Composable
fun GroupItem(group: AddGroupResponse, viewModel: GroupViewModel, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {
            navController.navigate(Screen.DetailedGroupScreen.createRoute(group.groupId))
        },
        modifier = Modifier
            .padding(4.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            group.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = url)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(100.dp) // Adjust size as needed
                        .padding(end = 8.dp)
                )
            }
            Column {
                Text(text = group.name, modifier = Modifier.padding(bottom = 4.dp))
                // You can add more details if needed
            }
        }
    }
}

@Composable
fun StartNewGroup(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.NewGroupScreen.route) },
    ) {
        Text(text = "Start a new group")
    }
}
