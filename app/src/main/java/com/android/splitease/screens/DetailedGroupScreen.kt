package com.android.splitease.screens

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun DetailedGroupScreen(groupId: Int) {
    val context = LocalContext.current
    Text(text = "This is group")
    Toast.makeText(context, groupId.toString(), Toast.LENGTH_SHORT).show()
}