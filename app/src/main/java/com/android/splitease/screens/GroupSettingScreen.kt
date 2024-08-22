package com.android.splitease.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Red800
import com.android.splitease.utils.ErrorDialog
import com.android.splitease.utils.LoadingOverlay
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
    val deleteGroup : State<NetworkResult<DeleteResponse>> = groupViewModel.deleteGroup.collectAsState()

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

    var showAlertOnDelete by remember { mutableStateOf(false) }
    var loading by remember {
        mutableStateOf(false)
    }

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

        Column(
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
                        
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                onClick = { navController.navigate(Screen.AddUsersToGroupScreen.createRoute(groupId)) },
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
                                ) {

                                    Surface(
                                        modifier = Modifier.size(45.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.GroupAdd,
                                                contentDescription = "Add users"
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))

                                    Text(text = "Add people")
                                }
                            }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(50.dp)
                    .clickable {
                        showAlertOnDelete = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete group",
                    tint = Red800,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(30.dp)
                )
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Red800)){
                        append("Delete Group")
                    }
                })
            }
        }

        // Show a loading overlay when removing a user
        if (showLoadingOverlay) {
            LoadingOverlay()
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

    if (showAlertOnDelete){
        AlertDialog(
            title = { Text(text = "Confirm Group Deletion") },
            text = { Text(text = "Are you sure you want to permanently delete this group? " +
                    "This action cannot be undone, and all group data will be lost. " +
                    "Once deleted, the group cannot be restored.") },
            onDismissRequest = { showAlertOnDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    showAlertOnDelete = false
                    groupViewModel.deleteGroup(groupId)
                }) {
                    Text("YES")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlertOnDelete = false
                }) {
                    Text("CANCEL")
                }
            }
        )
    }

    if (loading){
        LoadingOverlay()
    }

    LaunchedEffect(deleteGroup.value) {
        when(deleteGroup.value){
            is NetworkResult.Error -> {
                loading = false
            }
            is NetworkResult.Idle -> { loading = false }
            is NetworkResult.Loading -> { loading = true }
            is NetworkResult.Success -> {
                loading = false
                navController.navigate(Screen.GroupScreen.route) {
                    popUpTo(Screen.GroupScreen.route) { inclusive = true } // Clear back stack
                }
                Toast.makeText(context, "Group deleted successfully", Toast.LENGTH_SHORT).show()
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
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = member.name,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
        ) {
            // Circular icon with user's first initial
            Surface(
                modifier = Modifier.size(45.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = member.name.firstOrNull()?.toString()?.uppercase() ?: "",
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Column for user details
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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
