package com.android.splitease.viewmodels

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.service.chooser.ChooserAction
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun showDownloadNotification(context: Context, title: String, content: String, notificationId: Int, file: Uri?) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "file_actions_channel",
                "File Actions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for file actions notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open the file
        val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        //Showing two buttons -> open and share to allow user to either share or open or do both.
        // PendingIntent to open the file
        val openFilePendingIntent = PendingIntent.getActivity(
            context,
            0,
            openFileIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to share the file
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, file)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        // PendingIntent to share the file
        val sharePendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent.createChooser(shareIntent, "Share File"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build Notification with actions
        val notificationBuilder = NotificationCompat.Builder(context, "file_actions_channel").apply {
            setSmallIcon(R.drawable.download)
            setContentTitle(title)
            setContentText(content)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setOnlyAlertOnce(true)
            setAutoCancel(true)

            // Add actions
            addAction(NotificationCompat.Action(
                R.drawable.download,
                "Open",
                openFilePendingIntent
            ))
            addAction(NotificationCompat.Action(
                R.drawable.add_people,
                "Share",
                sharePendingIntent
            ))
        }

        // Notify
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}