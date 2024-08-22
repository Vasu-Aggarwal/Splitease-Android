package com.android.splitease.repositories

import android.Manifest
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.splitease.R
import com.android.splitease.di.NetworkException
import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.services.GroupService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GroupRepository @Inject constructor(private val groupService: GroupService, private val tokenManager: TokenManager) {
    private val _groups = MutableStateFlow<NetworkResult<List<GetGroupsByUserResponse>>>(NetworkResult.Idle())
    val groups: StateFlow<NetworkResult<List<GetGroupsByUserResponse>>>
        get() =_groups

    private val _groupMembers = MutableStateFlow<NetworkResult<List<CreateUserResponse>>>(NetworkResult.Idle())
    val groupMembers: StateFlow<NetworkResult<List<CreateUserResponse>>>
        get() = _groupMembers

    private val _groupMembersV2 = MutableStateFlow<NetworkResult<List<GetGroupMembersV2Response>>>(NetworkResult.Idle())
    val groupMembersV2: StateFlow<NetworkResult<List<GetGroupMembersV2Response>>>
        get() = _groupMembersV2

    private val _addUpdateGroup = MutableStateFlow<NetworkResult<AddGroupResponse>>(NetworkResult.Idle())
    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = _addUpdateGroup

    private val _addUsersToGroup = MutableStateFlow<NetworkResult<AddUsersToGroupResponse>>(NetworkResult.Idle())
    val addUsersToGroup: StateFlow<NetworkResult<AddUsersToGroupResponse>>
        get() = _addUsersToGroup

    private val _groupSummary = MutableStateFlow<NetworkResult<GetGroupSummaryResponse>>(NetworkResult.Idle())
    val groupSummary: StateFlow<NetworkResult<GetGroupSummaryResponse>>
        get() = _groupSummary

    private val _groupInfo = MutableStateFlow<NetworkResult<AddGroupResponse>>(NetworkResult.Idle())
    val groupInfo: StateFlow<NetworkResult<AddGroupResponse>>
        get() = _groupInfo

    private val _download = MutableStateFlow<NetworkResult<Boolean>>(NetworkResult.Idle())
    val download: StateFlow<NetworkResult<Boolean>>
        get() = _download

    private val _removeUser = MutableStateFlow<NetworkResult<DeleteResponse>>(NetworkResult.Idle())
    val removeUser: StateFlow<NetworkResult<DeleteResponse>>
        get() = _removeUser

    private val _deleteGroup = MutableStateFlow<NetworkResult<DeleteResponse>>(NetworkResult.Idle())
    val deleteGroup: StateFlow<NetworkResult<DeleteResponse>>
        get() = _deleteGroup

    suspend fun groupsByUser(searchBy: String){
        try {
            _groups.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val userUuid = tokenManager.getUserUuid()!!
            val response = groupService.getGroupsByUser("Bearer $authToken", userUuid, searchBy)
            if (response.isSuccessful && response.body() != null) {
                _groups.emit(NetworkResult.Success(response.body()!!))
            } else {
                _groups.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _groups.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _groups.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getGroupMembers(groupId: Int){
        try {
            _groupMembers.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = groupService.getGroupMembersApi("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _groupMembers.emit(NetworkResult.Success(response.body()!!))
            } else {
                _groupMembers.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _groupMembers.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _groupMembers.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getGroupMembersV2(groupId: Int){
        try {
            _groupMembersV2.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = groupService.getGroupMembersV2Api("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _groupMembersV2.emit(NetworkResult.Success(response.body()!!))
            } else {
                _groupMembersV2.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _groupMembersV2.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _groupMembersV2.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun addUpdateGroup(name: String, id: Int?, image: File?){
        try {
            _addUpdateGroup.emit(NetworkResult.Loading())
            // Create request body for text data
            val nameRequestBody = RequestBody.create(MultipartBody.FORM, name)

            // Create request body for id if id is not null
            val idRequestBody = id?.let { RequestBody.create(MultipartBody.FORM, it.toString()) }

            // Create multipart body for image if the image is not null or empty
            val imagePart = image?.takeIf { it.exists() && it.length() > 0 }?.let {
                val imageRequestBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, imageRequestBody)
            }

            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.addUpdateGroupApi(
                "Bearer $authToken",
                imagePart,
                nameRequestBody,
                idRequestBody
            )
            if (response.isSuccessful && response.body() != null) {
                _addUpdateGroup.emit(NetworkResult.Success(response.body()!!))
            } else {
                _addUpdateGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _addUpdateGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _addUpdateGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun addUsersToGroup(addUsersToGroupRequest: AddUsersToGroupRequest){
        try {
            _addUsersToGroup.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val response =
                groupService.addUsersToGroupApi("Bearer $authToken", addUsersToGroupRequest)
            if (response.isSuccessful && response.body() != null) {
                _addUsersToGroup.emit(NetworkResult.Success(response.body()!!))
            } else {
                _addUsersToGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _addUsersToGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _addUsersToGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun removeUserFromGroup(groupId: Int, userUuid: String){
        try {
            _removeUser.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.removeUserFromGroupApi("Bearer $authToken", groupId, userUuid)
            if (response.isSuccessful && response.body() != null) {
                _removeUser.emit(NetworkResult.Success(response.body()!!))
            } else {
                _removeUser.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _removeUser.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _removeUser.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getGroupSummary(groupId: Int){
        try {
            _groupSummary.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.getGroupSpendingSummaryApi("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _groupSummary.emit(NetworkResult.Success(response.body()!!))
            } else {
                _groupSummary.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _groupSummary.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _groupSummary.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getGroupInfo(groupId: Int){
        try {
            _groupInfo.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.getGroupInfoApi("Bearer $authToken", groupId)
            if (response.isSuccessful && response.body() != null) {
                _groupInfo.emit(NetworkResult.Success(response.body()!!))
            } else {
                _groupInfo.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _groupInfo.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _groupInfo.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun deleteGroup(groupId: Int){
        try {
            _deleteGroup.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val userUuid = tokenManager.getUserUuid()!!
            val response = groupService.deleteGroupApi("Bearer $authToken", groupId, userUuid)
            if (response.isSuccessful && response.body() != null) {
                _deleteGroup.emit(NetworkResult.Success(response.body()!!))
            } else {
                _deleteGroup.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _deleteGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _deleteGroup.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun downloadExcelFileToDownloads(context: Context, groupId: Int): Map<Boolean, Uri?> {
        return try {
            val authToken = tokenManager.getAuthToken()!!
            _download.emit(NetworkResult.Loading())
            val response = groupService.downloadExcel(authToken, groupId)

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    // Prepare details for the file to be saved in the Downloads directory
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    _download.emit(NetworkResult.Success(true))
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "transactions_${timeStamp}.xlsx")
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // Save to Downloads
                    }

                    val contentResolver = context.contentResolver
                    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                    uri?.let {
                        contentResolver.openOutputStream(uri)?.use { outputStream ->
                            body.byteStream().use { inputStream ->
                                val copiedBytes = inputStream.copyTo(outputStream)
                                Log.d("DownloadFile", "File copied: $copiedBytes bytes")
                            }
                        }
                        // Verify the file existence
                        val savedFile = File(uri.path)
                        Log.d("DownloadFile", "File saved at: ${savedFile.absolutePath}")
                        mapOf(true to uri)
                    } ?: run {
                        Log.e("DownloadFile", "Failed to create URI for file")
                        mapOf(false to null)
                    }
                } ?: run {
                    Log.e("DownloadFile", "Response body is null")
                    mapOf(false to null)
                }
            } else {
                _download.emit(NetworkResult.Success(false))
                Log.e("DownloadError", "Failed: ${response.code()} - ${response.message()}")
                mapOf(false to null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _download.emit(NetworkResult.Success(false))
            Log.e("DownloadFile", "Exception occurred: ${e.message}")
            mapOf(false to null)
        }
    }

}