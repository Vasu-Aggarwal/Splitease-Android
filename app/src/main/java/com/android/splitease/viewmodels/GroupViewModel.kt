package com.android.splitease.viewmodels

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.R
import com.android.splitease.models.requests.AddUsersToGroupRequest
import com.android.splitease.models.responses.AddGroupResponse
import com.android.splitease.models.responses.AddUsersToGroupResponse
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetGroupMembersV2Response
import com.android.splitease.models.responses.GetGroupSummaryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val groupRepository: GroupRepository) : ViewModel() {
    val groups: StateFlow<NetworkResult<List<GetGroupsByUserResponse>>>
        get() = groupRepository.groups

    val groupMembers: StateFlow<NetworkResult<List<CreateUserResponse>>>
        get() = groupRepository.groupMembers

    val groupMembersV2: StateFlow<NetworkResult<List<GetGroupMembersV2Response>>>
        get() = groupRepository.groupMembersV2

    val addUpdateGroup: StateFlow<NetworkResult<AddGroupResponse>>
        get() = groupRepository.addUpdateGroup

    val addUsersToGroup: StateFlow<NetworkResult<AddUsersToGroupResponse>>
        get() = groupRepository.addUsersToGroup

    val removeUser: StateFlow<NetworkResult<DeleteResponse>>
        get() = groupRepository.removeUser

    val deleteGroup: StateFlow<NetworkResult<DeleteResponse>>
        get() = groupRepository.deleteGroup

    val groupSummary: StateFlow<NetworkResult<GetGroupSummaryResponse>>
        get() = groupRepository.groupSummary

    val groupInfo: StateFlow<NetworkResult<AddGroupResponse>>
        get() = groupRepository.groupInfo

    val download: StateFlow<NetworkResult<Boolean>>
        get() = groupRepository.download

    fun getGroupsByUser(searchBy: String) {
        viewModelScope.launch {
            groupRepository.groupsByUser(searchBy)
        }
    }

    fun getGroupMembers(groupId: Int) {
        viewModelScope.launch {
            groupRepository.getGroupMembers(groupId)
        }
    }

    fun getGroupMembersV2(groupId: Int) {
        viewModelScope.launch {
            groupRepository.getGroupMembersV2(groupId)
        }
    }

    fun addUpdateGroup(name: String, id: Int?, image: File?){
        viewModelScope.launch {
            groupRepository.addUpdateGroup(name, id, image)
        }
    }

    fun addUsersToGroup(addUsersToGroupRequest: AddUsersToGroupRequest){
        viewModelScope.launch {
            groupRepository.addUsersToGroup(addUsersToGroupRequest)
        }
    }

    fun removeUserFromGroup(groupId: Int, userUuid: String){
        viewModelScope.launch {
            groupRepository.removeUserFromGroup(groupId, userUuid)
        }
    }

    fun deleteGroup(groupId: Int){
        viewModelScope.launch {
            groupRepository.deleteGroup(groupId)
        }
    }

    fun getGroupSummary(groupId: Int){
        viewModelScope.launch {
            groupRepository.getGroupSummary(groupId)
        }
    }

    fun getGroupInfo(groupId: Int){
        viewModelScope.launch {
            groupRepository.getGroupInfo(groupId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadExcel(context: Context, groupId: Int){
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val success = withContext(Dispatchers.IO) {
                    // Replace with your actual download logic
                    val fileDownloaded = groupRepository.downloadExcelFileToDownloads(context, groupId)
                    fileDownloaded
                }
                // Show notification when the download is complete
                if (success.containsKey(false)){
                    showDownloadNotification(context, "Download Failed", "An error occurred", 1, success[success.keys.first()])
                } else {
                    showDownloadNotification(context, "Download Complete", "Tap to open the file", 2,
                        success[success.keys.first()]
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDownloadNotification(context, "Download Failed", "An error occurred: ${e.message}", 1, null)
            }
        }
    }

    private fun showDownloadNotification(context: Context, title: String, content: String, notificationId: Int, file: Uri?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound URI (optional, but replace with your own sound)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "File Download",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for file download notifications"
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT) // Set the sound for the notification channel
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create Intent to open the file
        val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        // Create PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openFileIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build Notification
        val notificationBuilder = NotificationCompat.Builder(context, "download_channel").apply {
            setSmallIcon(R.drawable.download)
            setContentTitle(title) // Ensure `title` is not null
            setContentText(content) // Ensure `content` is not null
            setPriority(NotificationCompat.PRIORITY_LOW)
            setOnlyAlertOnce(true)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setSound(soundUri) // Set sound for the notification
        }

        // Notify
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}