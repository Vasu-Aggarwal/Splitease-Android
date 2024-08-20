package com.android.splitease.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.GetUserLogsResponse
import com.android.splitease.models.util.LogDetailedJson
import com.android.splitease.ui.theme.Grey400
import com.android.splitease.utils.ActivityType
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.android.splitease.viewmodels.UserViewModel
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {  },
            )
        }
    ){ padding ->
        when (val result = userActivities.value) {
            is NetworkResult.Error -> {}
            is NetworkResult.Idle -> {}
            is NetworkResult.Loading -> {}
            is NetworkResult.Success -> {
                result.data!!.let {
                    LazyColumn(
                        modifier = Modifier.padding(padding)
                    ) {
                        item{
                            Text(text = "Activity", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        }
                        items(it) { userLog ->
                            ActivityItem(userLog = userLog)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(userLog: GetUserLogsResponse){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {
            when (userLog.activityType) {
                //Add group
                ActivityType.ADD_GROUP.name -> {  }

                //Add user to group
                ActivityType.ADD_USER_TO_GROUP.name -> {  }

                //Remove user from the group
                ActivityType.REMOVE_USER_FROM_GROUP.name -> {  }

                //Delete the group
                ActivityType.DELETE_GROUP.name -> {  }

                //Add transaction
                ActivityType.ADD_TRANSACTION.name -> {  }

                //When transaction is settled
                ActivityType.SETTLED.name -> {  }

                //When transaction is deleted
                ActivityType.DELETE_TRANSACTION.name -> {  }
            }
        }
    ) {
        val gson = Gson()
        val groupDetails = gson.fromJson(userLog.details, LogDetailedJson::class.java)
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val formattedDate = formatCreatedOnDate(userLog.createdOn)
            Text(text = groupDetails.description!!)
            Text(text = formattedDate, fontSize = 12.sp, color = Grey400)
        }
    }
}

@Composable
fun formatCreatedOnDate(createdOn: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val createdDateTime = LocalDateTime.parse(createdOn, formatter)
    val currentDateTime = LocalDateTime.now()

    val daysBetween = ChronoUnit.DAYS.between(createdDateTime.toLocalDate(), currentDateTime.toLocalDate())

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val formattedTime = createdDateTime.format(timeFormatter)

    val datePart = when {
        daysBetween == 0L -> "Today"
        daysBetween == 1L -> "Yesterday"
        daysBetween in 2..3 -> "$daysBetween days ago"
        else -> createdDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }

    return "$datePart, $formattedTime"
}