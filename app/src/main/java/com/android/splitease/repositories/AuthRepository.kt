package com.android.splitease.repositories

import android.util.Log
import com.android.splitease.di.NetworkException
import com.android.splitease.models.requests.RefreshTokenRequest
import com.android.splitease.models.requests.RegisterUserRequest
import com.android.splitease.models.requests.UserLoginRequest
import com.android.splitease.models.responses.CreateUserResponse
import com.android.splitease.models.responses.ErrorResponse
import com.android.splitease.models.responses.UserLoginResponse
import com.android.splitease.services.AuthService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService,
                                         private val tokenManager: TokenManager
){

    private val _user = MutableStateFlow<NetworkResult<UserLoginResponse>>(NetworkResult.Idle())
    val user : StateFlow<NetworkResult<UserLoginResponse>>
        get() = _user

    private val _registerUser = MutableStateFlow<NetworkResult<CreateUserResponse>>(NetworkResult.Idle())
    val registerUser : StateFlow<NetworkResult<CreateUserResponse>>
        get() = _registerUser

    suspend fun login(email: String, password: String){
        try {
            _user.emit(NetworkResult.Loading())
            val requestBody = UserLoginRequest(email, password)
            val response = authService.loginUser(requestBody)
            if (response.isSuccessful && response.body() != null) {
                val authToken = response.body()!!.token
                val refreshToken = response.body()!!.refreshToken
                val userUuid = response.body()!!.userUuid
                tokenManager.saveAuthToken(authToken, refreshToken, userUuid)
                _user.emit(NetworkResult.Success(response.body()!!))
            } else {
                // Parse the error body to extract the error message
                val rawError = response.errorBody()?.string()
                val errorResponse = rawError.let { errorBody ->
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    gson.fromJson<ErrorResponse>(errorBody, type)
                }
                errorResponse?.let {
                    _user.emit(NetworkResult.Error(errorResponse.message))
                } ?: run {
                    Log.i("Error", "login: ${response.code()}")
                }
            }
        } catch (e: NetworkException){
            _user.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _user.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun refreshToken(){
        try{
            val requestBody = RefreshTokenRequest(tokenManager.getRefreshToken()!!)
            val response = authService.refreshToken(requestBody)
            if (response.isSuccessful && response.body() != null) {
                val authToken = response.body()!!.token
                tokenManager.updateAuthToken(authToken)
            } else {
                // Parse the error body to extract the error message
                val rawError = response.errorBody()?.string()
                Log.e("Refresh Token", "refreshToken: caused the error$rawError")
            }
        } catch (e: NetworkException){
            Log.d("Update JWT", "refreshToken: ${e.message}")
        } catch (e: Exception){
            Log.d("Update JWT", "refreshToken: ${e.message}")
        }
    }

    suspend fun registerNewUser(registerUserRequest: RegisterUserRequest){
        try{
            _registerUser.emit(NetworkResult.Loading())
            val requestBody = registerUserRequest
            val response = authService.registerUserApi(requestBody)
            if (response.isSuccessful && response.body() != null) {
                _registerUser.emit(NetworkResult.Success(response.body()!!))
            } else {
                _registerUser.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _registerUser.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            Log.d("Update JWT", "registerNewUser: ${e.message}")
        }
    }
}