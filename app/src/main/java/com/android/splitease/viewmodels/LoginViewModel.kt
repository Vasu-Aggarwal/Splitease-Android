package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.responses.UserLoginResponse
import com.android.splitease.repositories.AuthRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    val user: StateFlow<NetworkResult<UserLoginResponse>>
        get() = repository.user

    fun login(username: String, password: String){
        viewModelScope.launch {
            repository.login(username, password)
        }
    }

}