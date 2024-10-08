package com.android.splitease.repositories

import com.android.splitease.di.NetworkException
import com.android.splitease.local.dao.UserDao
import com.android.splitease.local.entity.UserEntity
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetOverallUserBalance
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.models.responses.GetUserLogsResponse
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

    private val _userBalance = MutableStateFlow<NetworkResult<GetOverallUserBalance>>(
        NetworkResult.Idle())
    val userBalance: StateFlow<NetworkResult<GetOverallUserBalance>>
        get() = _userBalance

    private val _isUserExists = MutableStateFlow<NetworkResult<List<GetUserByUuidResponse>>>(
        NetworkResult.Idle())
    val isUserExists: StateFlow<NetworkResult<List<GetUserByUuidResponse>>>
        get() = _isUserExists

    private val _userActivities = MutableStateFlow<NetworkResult<List<GetUserLogsResponse>>>(
        NetworkResult.Idle())
    val userActivities: StateFlow<NetworkResult<List<GetUserLogsResponse>>>
        get() = _userActivities

    suspend fun getUserByUuid(userUuid: String){
        try {
            _user.emit(NetworkResult.Loading())
            // Try fetching from local cache first
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
        } catch (e: Exception){
            _user.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getOverallUserBalance(searchVal: String){
        try {
            _userBalance.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val userUuid = tokenManager.getUserUuid()
            val response =
                userService.getOverallUserBalanceApi("Bearer $authToken", userUuid!!, searchVal)
            if (response.isSuccessful && response.body() != null) {
                _userBalance.emit(NetworkResult.Success(response.body()!!))
            } else {
                _userBalance.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _userBalance.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _userBalance.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun isUserExists(userData: String){
        try {
            _isUserExists.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = userService.isUserExistsApi("Bearer $authToken", userData)
            if (response.isSuccessful && response.body() != null) {
                _isUserExists.emit(NetworkResult.Success(response.body()!!))
            } else {
                _isUserExists.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _isUserExists.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _isUserExists.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getUserActivities(userUuid: String){
        try {
            _userActivities.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = userService.getUserActivitiesApi("Bearer $authToken", userUuid)
            if (response.isSuccessful && response.body() != null) {
                _userActivities.emit(NetworkResult.Success(response.body()!!))
            } else {
                _userActivities.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _userActivities.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _userActivities.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
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
        email = this.email,
        mobile = this.email
    )
}