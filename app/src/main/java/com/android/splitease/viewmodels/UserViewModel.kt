package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.GetOverallUserBalance
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.repositories.UserRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    val user: StateFlow<NetworkResult<GetUserByUuidResponse>>
        get() = userRepository.user

    val userBalance: StateFlow<NetworkResult<GetOverallUserBalance>>
        get() = userRepository.userBalance

    suspend fun getUserByUuid(userUuid: String) {
        userRepository.getUserByUuid(userUuid)
    }

    suspend fun getOverallUserBalance(){
        userRepository.getOverallUserBalance()
    }

}