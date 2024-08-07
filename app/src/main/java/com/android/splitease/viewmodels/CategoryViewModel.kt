package com.android.splitease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.repositories.CategoryRepository
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) : ViewModel() {
    val categories: StateFlow<NetworkResult<List<GetCategoryResponse>>>
        get() = categoryRepository.categories

    fun getCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
        }
    }
}