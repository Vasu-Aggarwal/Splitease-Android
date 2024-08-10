package com.android.splitease.repositories

import android.util.Log
import com.android.splitease.di.NetworkException
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.services.CategoryService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val categoryService: CategoryService, private val tokenManager: TokenManager){

    private val _categories = MutableStateFlow<NetworkResult<List<GetCategoryResponse>>>(
        NetworkResult.Idle())
    val categories: StateFlow<NetworkResult<List<GetCategoryResponse>>>
        get() = _categories

    suspend fun getCategories(){
        try {
            _categories.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = categoryService.getCategories("Bearer $authToken")
            if (response.isSuccessful && response.body() != null) {
                _categories.emit(NetworkResult.Success(response.body()!!))
            } else {
                _categories.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _categories.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _categories.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

}