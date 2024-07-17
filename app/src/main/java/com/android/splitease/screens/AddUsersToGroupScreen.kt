package com.android.splitease.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AddUsersToGroupScreen(groupId: Int){
    Text(text = "Group Id i am receiving is: $groupId")
}