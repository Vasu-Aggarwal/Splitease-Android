package com.android.splitease.screens

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.splitease.R
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.utils.NetworkResult
import com.android.splitease.viewmodels.GroupViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
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
    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                imageUri = resultUri
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = uri
            // Start UCrop activity for cropping
            val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image.jpg"))
            val uCropIntent = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f) // Set aspect ratio here if needed
                .getIntent(context)
            cropLauncher.launch(uCropIntent)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create a group",
                        style = MaterialTheme.typography.titleLarge, // Adjust text style if needed
                    )
                },
                navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Convert Uri to File if necessary and pass it to the ViewModel
                        val imageFile = imageUri?.let { uri ->
                            convertUriToFile(context, uri)
                        }
                        imageFile?.let {
                            groupViewModel.addUpdateGroup(groupName, null, it)
                        }
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .fillMaxHeight(0.11f)
            )
        }
    ) { padding ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(16.dp)
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    IconButton(onClick = {
                        launcher.launch("image/*") // Open image picker
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_photo),
                            contentDescription = "Pick Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Enter Group Name") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

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
                    Text(text = result.message ?: "Unknown error occurred", color = MaterialTheme.colorScheme.error)
                }
                is NetworkResult.Loading -> {
                    // Show loading state if needed
                    Text(text = "Creating group...")
                }
                is NetworkResult.Idle -> Unit
            }
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

