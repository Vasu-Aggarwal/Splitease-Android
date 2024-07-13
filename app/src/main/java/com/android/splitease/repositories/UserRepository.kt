package com.android.splitease.repositories

import com.android.splitease.di.NetworkException
import com.android.splitease.local.dao.UserDao
import com.android.splitease.local.entity.UserEntity
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.services.UserService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserService,
                                          private val tokenManager: TokenManager,
                                         private val userDao: UserDao
) {
    private val _user = MutableStateFlow<NetworkResult<GetUserByUuidResponse>>(
        NetworkResult.Idle())
    val user: StateFlow<NetworkResult<GetUserByUuidResponse>>
        get() = _user

    suspend fun getUserByUuid(userUuid: String){
        try {// Try fetching from local cache first
            val cachedUser = withContext(Dispatchers.IO) {
                userDao.getUserByUuidWithTTL(
                    userUuid,
                    System.currentTimeMillis() - AppConstants.CACHE_TTL
                )
            }

            if (cachedUser != null) {
                _user.emit(NetworkResult.Success(cachedUser.toGetUserByUuidResponse()))
            } else {
                val authToken = tokenManager.getAuthToken()
                val response = userService.getUserByUuidAPi("Bearer $authToken", userUuid)
                if (response.isSuccessful && response.body() != null) {
                    _user.emit(NetworkResult.Success(response.body()!!))
                    // Save to cache
                    withContext(Dispatchers.IO) {
                        deleteStaleUsers()  // Clean up stale entries after inserting new data
                        val newUser = response.body()!!.toUserEntity()
                        newUser.lastUpdated = System.currentTimeMillis() // Update timestamp
                        userDao.insertUser(newUser)
                    }
                } else {
                    _user.emit(NetworkResult.Error(response.errorBody()?.string()!!))
                }
            }
        } catch (e: NetworkException){
            _user.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    private suspend fun deleteStaleUsers() {
        val maxStaleTimestamp = System.currentTimeMillis() - AppConstants.CACHE_TTL
        withContext(Dispatchers.IO) {
            userDao.deleteStaleUsers(maxStaleTimestamp)
        }
    }
}

// Extension function to convert from CreateUserResponse to UserEntity
fun GetUserByUuidResponse.toUserEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        email = this.email,
        uuid = this.userUuid,
        lastUpdated = System.currentTimeMillis()
    )
}

// Extension function to convert from UserEntity to CreateUserResponse
fun UserEntity.toGetUserByUuidResponse(): GetUserByUuidResponse {
    return GetUserByUuidResponse(
        name = this.name,
        userUuid = this.uuid,
        email = this.email
    )
}