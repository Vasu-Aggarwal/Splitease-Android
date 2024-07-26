package com.android.splitease.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun NewGroupScreen(
    groupViewModel: GroupViewModel = hiltViewModel(),
    navController: NavController
) {
    var groupName by remember { mutableStateOf("") }
    val addUpdateGroup : State<NetworkResult<AddGroupResponse>> = groupViewModel.addUpdateGroup.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Activity result launcher for image picking
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = uri
            // Optionally, you can convert uri to File or upload it directly
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Enter Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                launcher.launch("image/*") // Open image picker
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Convert Uri to File if necessary and pass it to the ViewModel
                val imageFile = imageUri?.let { uri ->
                    convertUriToFile(context, uri)
                }
                imageFile?.let {
                    groupViewModel.addUpdateGroup(groupName, null, it)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }

        // Handling response
        when (val result = addUpdateGroup.value) {
            is NetworkResult.Success -> {
                // Navigate when success response is received
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.DetailedGroupScreen.createRoute(result.data!!.groupId)) // Replace with your target screen
                }
            }
            is NetworkResult.Error -> {
                // Handle error
                Text(text = result.message ?: "Unknown error occurred")
            }
            is NetworkResult.Loading -> {
                // Show loading state if needed
                Text(text = "Creating group...")
            }

            is NetworkResult.Idle -> Text(text = "Idle")
        }
    }
}

fun convertUriToFile(context: Context, uri: Uri): File? {
    val inputStream = getInputStreamFromUri(context, uri) ?: return null
    val tempFile = File(context.cacheDir, "temp_image.jpg")
    val outputStream = FileOutputStream(tempFile)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return tempFile
}

fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
    return try {
        context.contentResolver.openInputStream(uri)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

