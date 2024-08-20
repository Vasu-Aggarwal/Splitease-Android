package com.android.splitease.screens

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetUserLogsResponse
import com.android.splitease.utils.ActivityType
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.viewmodels.UserViewModel

@Composable
fun ActivityScreen(navController: NavController, userViewModel: UserViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val tokenManager = TokenManager(sharedPreferences)
    val userUuid = tokenManager.getUserUuid()

    val userActivities: State<NetworkResult<List<GetUserLogsResponse>>> = userViewModel.userActivities.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUserActivities(userUuid!!)
    }

    when(val result = userActivities.value) {
        is NetworkResult.Error -> {}
        is NetworkResult.Idle -> {}
        is NetworkResult.Loading -> {}
        is NetworkResult.Success -> {
            result.data!!.let {
                LazyColumn {
                    items(it) { userLog ->
                        when (userLog.activityType) {
                            //Add group
                            ActivityType.ADD_GROUP.name -> {
                                AddGroupActivity(userLog)
                            }

                            //Add user to group
                            ActivityType.ADD_USER_TO_GROUP.name -> {
                                AddUserToGroupActivity(userLog)
                            }

                            //Remove user from the group
                            ActivityType.REMOVE_USER_FROM_GROUP.name -> {
                                RemoveUserFromGroupActivity(userLog)
                            }

                            //Delete the group
                            ActivityType.DELETE_GROUP.name -> {
                                DeleteGroupActivity(userLog)
                            }

                            //Add transaction
                            ActivityType.ADD_TRANSACTION.name -> {
                                AddTransactionActivity(userLog)
                            }

                            //When transaction is settled
                            ActivityType.SETTLED.name -> {
                                SettleTransactionActivity(userLog)
                            }

                            //When transaction is deleted
                            ActivityType.DELETE_TRANSACTION.name -> {
                                DeleteTransactionActivity(userLog)
                            }
                        }
                    }
                }
            }
        }
    }
}