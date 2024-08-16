package com.android.splitease.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun GroupSettingScreen(navController: NavController, groupId: Int, groupViewModel: GroupViewModel = hiltViewModel()) {

    val groupMembers: State<NetworkResult<List<GetGroupMembersV2Response>>> = groupViewModel.groupMembersV2.collectAsState()
    val removeUser: State<NetworkResult<DeleteResponse>> = groupViewModel.removeUser.collectAsState()

    // State to control the bottom sheet
    var selectedMember by remember { mutableStateOf<GetGroupMembersV2Response?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Boolean state to control the visibility of the bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        groupViewModel.getGroupMembersV2(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier.padding(padding)
        ) {
            when (val result = groupMembers.value) {
                is NetworkResult.Error -> {
                    // Handle error state
                    Text("Error loading group members")
                }
                is NetworkResult.Idle -> {
                    // Handle idle state if necessary
                }
                is NetworkResult.Loading -> {
                    // Handle loading state
                    Text("Loading group members...")
                }
                is NetworkResult.Success -> {
                    LazyColumn {
                        stickyHeader {
                            Text(
                                text = "Group Members",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }

                        result.data?.let {
                            items(it) { member ->
                                GroupMemberItem(member) {
                                    selectedMember = member
                                    showBottomSheet = true // Set this to true when a member is clicked
                                    scope.launch {
                                        sheetState.show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Only show the bottom sheet if showBottomSheet is true
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
//                        sheetState.hide()
                        showBottomSheet = false // Reset this when the bottom sheet is dismissed
                    }
                },
                sheetState = sheetState,
            ) {
                selectedMember?.let {
                    BottomSheetContent(member = it) {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false // Reset this when the bottom sheet is dismissed
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupMemberItem(
    member: GetGroupMembersV2Response,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun BottomSheetContent(
    member: GetGroupMembersV2Response,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Member Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Name: ${member.name}")
        Text(text = "Email: ${member.email}")
    }
}