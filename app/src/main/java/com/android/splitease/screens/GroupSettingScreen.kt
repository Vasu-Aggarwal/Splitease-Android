package com.android.splitease.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.ui.theme.Red800
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.utils.UtilMethods
import com.android.splitease.viewmodels.GroupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun GroupSettingScreen(navController: NavController, groupId: Int, groupViewModel: GroupViewModel = hiltViewModel()) {

    val groupMembers: State<NetworkResult<List<GetGroupMembersV2Response>>> = groupViewModel.groupMembersV2.collectAsState()
    val removeUser by groupViewModel.removeUser.collectAsState()

    // State to control the bottom sheet
    var selectedMember by remember { mutableStateOf<GetGroupMembersV2Response?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // State to control the loading overlay
    var showLoadingOverlay by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val userUuid = tokenManager.getUserUuid()

    // Observe the remove user state to hide the loading overlay once the operation is complete
    LaunchedEffect(removeUser) {
        if (removeUser is NetworkResult.Success) {
            groupViewModel.getGroupMembersV2(groupId)
            showLoadingOverlay = false
            Toast.makeText(context, "User removed from the group", Toast.LENGTH_SHORT).show()
        } else if (removeUser is NetworkResult.Error) {
            showLoadingOverlay = false
        }
    }

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

        // Show a loading overlay when removing a user
        if (showLoadingOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Only show the bottom sheet if showBottomSheet is true
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
//                        sheetState.hide()
                        showBottomSheet = false // Reset this when the bottom sheet is dismissed
                        selectedMember = null
                    }
                },
                sheetState = sheetState,
            ) {
                selectedMember?.let {
                    BottomSheetContent(member = it, userUuid, onRemoveClick = {
                        showLoadingOverlay = true
                        groupViewModel.removeUserFromGroup(groupId, selectedMember!!.userUuid)
                    }) {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false // Reset this when the bottom sheet is dismissed
                            selectedMember = null
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
    userUuid: String?,
    onRemoveClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Row for the remove action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onRemoveClick() // Trigger the removal action when the row is clicked
                    onDismiss() // Dismiss the bottom sheet after the action
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                contentDescription = "Remove user",
                tint = Red800,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(30.dp)
            )
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Red800)){
                    if (member.userUuid.equals(userUuid, true))
                        append("Leave group")
                    else
                        append("Remove ${UtilMethods.abbreviateName(member.name)} from the group")
                }
            })
        }
    }
}
