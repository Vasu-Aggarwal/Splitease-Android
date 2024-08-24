package com.android.splitease.screens

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.splitease.R
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.navigation.Screen
import com.android.splitease.ui.theme.Grey200
import com.android.splitease.ui.theme.Grey800
import com.android.splitease.utils.LoadingOverlay
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
    navController: NavController,
    mode: String,
    groupId: Int?
) {
    var groupName by remember { mutableStateOf("") }
    val addUpdateGroup : State<NetworkResult<AddGroupResponse>> = groupViewModel.addUpdateGroup.collectAsState()
    val groupInfo: State<NetworkResult<AddGroupResponse>> = groupViewModel.groupInfo.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showLoadingOverlay by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var nameError by remember { mutableStateOf("") }

    if (mode.equals("update", ignoreCase = true) && groupId != null && groupId != 0) {
        LaunchedEffect(groupId) {
            groupViewModel.getGroupInfo(groupId);
        }
    }

    when(val result = groupInfo.value){
        is NetworkResult.Error -> {}
        is NetworkResult.Idle -> {}
        is NetworkResult.Loading -> {}
        is NetworkResult.Success -> {
            groupName = result.data?.name ?: ""
            imageUri = result.data?.imageUrl?.let { Uri.parse(it) }
        }
    }

    var shouldShowPermissionDialog by remember { mutableStateOf(false) }

    // Activity result launcher for image picking
    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                imageUri = resultUri
            }
        } else if (result.resultCode == 0) {
            // Reset imageUri when cropping is canceled
            imageUri = null
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = uri
            // Start UCrop activity for cropping
            val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image.jpg"))
            val uCropIntent = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f)
                .getIntent(context)
            cropLauncher.launch(uCropIntent)
        }
    }

    // Permission launcher for requesting permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            shouldShowPermissionDialog = true
        }
    }

    // Function to check permission before launching image picker
    fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                launcher.launch("image/*")
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                launcher.launch("image/*")
            }
        }
    }

    if (shouldShowPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { shouldShowPermissionDialog = false },
            onConfirm = { checkAndRequestPermission() },
            onGoToSettings = { openAppSettings(context) }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (mode.equals("add", ignoreCase = true)) "Create a group" else "Customize group",
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
                            var valid = true

                            if (groupName.isNullOrBlank()){
                                nameError = "Group name cannot be empty"
                                valid = false
                            } else {
                                nameError = ""
                            }
                            if (valid){
                                showLoadingOverlay = true
                                // Convert Uri to File if necessary and pass it to the ViewModel
                                val imageFile = imageUri?.let { uri ->
                                    convertUriToFile(context, uri)
                                }

                                if (groupId == null || groupId == 0){
                                    groupViewModel.addUpdateGroup(groupName, null, imageFile)
                                } else {
                                    groupViewModel.addUpdateGroup(groupName, groupId, imageFile)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Done"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .fillMaxHeight(0.14f)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 15.dp, top = 7.dp)
                            .size(55.dp)
                            .border(1.dp, color = Grey200, shape = RoundedCornerShape(8.dp))
                            .background(Grey800, RoundedCornerShape(8.dp))
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUri),
                                contentDescription = "Selected Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        checkAndRequestPermission()
                                    }
                            )
                        } else {
                            IconButton(onClick = {
                                checkAndRequestPermission()
                            }, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_photo),
                                    contentDescription = "Pick Image",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (nameError.isNotEmpty()) {
                            Text(text = nameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            maxLines = 1,
                            singleLine = true,
                            label = { Text("Enter Group Name") },
                            isError = nameError.isNotEmpty(),
                            trailingIcon = {
                                if (nameError.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Filled.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                }

                // Handling response
                when (val result = addUpdateGroup.value) {
                    is NetworkResult.Success -> {
                        showLoadingOverlay = false
                        // Navigate when success response is received
                        LaunchedEffect(Unit) {
                            navController.navigate(Screen.DetailedGroupScreen.createRoute(result.data!!.groupId)) // Replace with your target screen
                        }
                    }

                    is NetworkResult.Error -> {
                        showLoadingOverlay = false
                        // Handle error
                        Text(
                            text = result.message ?: "Unknown error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    is NetworkResult.Loading -> {
                        showLoadingOverlay = true
                    }

                    is NetworkResult.Idle -> {
                        showLoadingOverlay = false
                    }
                }
            }
        }

        if (showLoadingOverlay) {
            LoadingOverlay()
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

@Composable
fun PermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onGoToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Permission Required") },
        text = { Text(text = "This app requires permission to access your images. Please grant the permission to continue.") },
        confirmButton = {
            TextButton(onClick = onGoToSettings) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:${context.packageName}")
    context.startActivity(intent)
}

