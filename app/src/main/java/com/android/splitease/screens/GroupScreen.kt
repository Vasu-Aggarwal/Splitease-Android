package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel

@Composable
fun GroupScreen(viewModel: GroupViewModel = hiltViewModel(), navController: NavController) {
    val groups: State<NetworkResult<List<AddGroupResponse>>> = viewModel.groups.collectAsState()
    val context = LocalContext.current
    Column{
        LazyColumn {
            groups.value.data?.let { groupList ->
                items(groupList) { group ->
                    GroupItem(group = group, viewModel, navController)
                }
            }
        }
        StartNewGroup(viewModel)
    }
}

@Composable
fun GroupItem(group: AddGroupResponse, viewModel: GroupViewModel, navController: NavController) {
    Card(
        onClick = {
            navController.navigate("detailedGroup/${group.groupId}")
        },
        modifier = Modifier
            .padding(4.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Row {
            Column {
                Text(text = group.name)
            }
        }
    }
}

@Composable
fun StartNewGroup(viewModel: GroupViewModel) {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Start a new group")
    }
}
