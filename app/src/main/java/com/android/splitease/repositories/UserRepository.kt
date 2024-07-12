package com.android.splitease.repositories

import com.android.splitease.local.dao.UserDao
import com.android.splitease.local.entity.UserEntity
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.services.UserService
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
    private val _user = MutableStateFlow<NetworkResult<CreateUserResponse>>(
        NetworkResult.Idle())
    val user: StateFlow<NetworkResult<CreateUserResponse>>
        get() = _user

    suspend fun getUserByUuid(userUuid: String){
        // Try fetching from local cache first
        val cachedUser = withContext(Dispatchers.IO) {
            userDao.getUserByUuid(userUuid)
        }

        if (cachedUser != null) {
            _user.emit(NetworkResult.Success(cachedUser.toCreateUserResponse()))
        } else {
            val authToken = tokenManager.getAuthToken()
            val response = userService.getUserByUuidAPi("Bearer $authToken", userUuid)
            if (response.isSuccessful && response.body() != null) {
                _user.emit(NetworkResult.Success(response.body()!!))
                // Save to cache
                withContext(Dispatchers.IO) {
                    userDao.insertUser(response.body()!!.toUserEntity())
                }
            } else {
                _user.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        }
    }
}

// Extension function to convert from CreateUserResponse to UserEntity
fun CreateUserResponse.toUserEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        email = this.name,
        uuid = this.userUuid
    )
}

// Extension function to convert from UserEntity to CreateUserResponse
fun UserEntity.toCreateUserResponse(): CreateUserResponse {
    return CreateUserResponse(
        name = this.name,
        userUuid = this.uuid
    )
}