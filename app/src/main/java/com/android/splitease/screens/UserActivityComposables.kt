package com.android.splitease.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.splitease.models.responses.GetUserLogsResponse
import com.android.splitease.models.util.LogDetailedJson
import com.google.gson.Gson

@Composable
fun CommonActivityLayout(
    title: String,
    createdOn: String,
    content: @Composable () -> Unit
) {
    // Common UI design
    androidx.compose.material3.Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()  // Specific content
            Text(text = createdOn, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun AddGroupActivity(userLog: GetUserLogsResponse) {
    val gson = Gson()
    val groupDetails = gson.fromJson(userLog.details, LogDetailedJson::class.java)


}

@Composable
fun AddUserToGroupActivity(userLog: GetUserLogsResponse) {

}

@Composable
fun RemoveUserFromGroupActivity(userLog: GetUserLogsResponse) {

}

@Composable
fun DeleteGroupActivity(userLog: GetUserLogsResponse) {

}

@Composable
fun AddTransactionActivity(userLog: GetUserLogsResponse) {

}

@Composable
fun SettleTransactionActivity(userLog: GetUserLogsResponse) {

}

@Composable
fun DeleteTransactionActivity(userLog: GetUserLogsResponse) {

}
