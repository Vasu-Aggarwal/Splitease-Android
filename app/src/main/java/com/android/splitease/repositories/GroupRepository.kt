package com.android.splitease.repositories

import android.content.Context
import android.util.Log
import com.android.splitease.di.NetworkException
import com.android.splitease.models.requests.AddGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
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

    suspend fun addUpdateGroup(name: String, id: Int?, image: File){
        try {
            _addUpdateGroup.emit(NetworkResult.Loading())
            // Create request body for text data
            val nameRequestBody = RequestBody.create(MultipartBody.FORM, name)

            // Create request body for id if id is not null
            val idRequestBody = id?.let { RequestBody.create(MultipartBody.FORM, it.toString()) }

            // Create multipart body for image
            val imagePart = image.let {
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

    suspend fun downloadExcelFile(context: Context, groupId: Int): File? {
        return try {
            _groupInfo.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()!!
            val response = groupService.downloadExcel(authToken, groupId)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    // Get the current timestamp
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                        Date()
                    )
                    val file = File(context.getExternalFilesDir(null), "transactions_${timeStamp}.xlsx")
                    body.byteStream().use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    file
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}