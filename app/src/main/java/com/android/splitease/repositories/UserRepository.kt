package com.android.splitease.repositories

import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.services.UserService
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserService,
                                          private val tokenManager: TokenManager) {
    private val _user = MutableStateFlow<NetworkResult<CreateUserResponse>>(
        NetworkResult.Idle())
    val user: StateFlow<NetworkResult<CreateUserResponse>>
        get() = _user

    suspend fun getUserByUuid(userUuid: String){
        val authToken = tokenManager.getAuthToken()
        val response = userService.getUserByUuidAPi("Bearer $authToken", userUuid)
        if (response.isSuccessful && response.body()!=null){
            _user.emit(NetworkResult.Success(response.body()!!))
        } else {
            _user.emit(NetworkResult.Error(response.errorBody()?.string()!!))
        }
    }
}