package com.android.splitease.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.splitease.local.entity.SubCategoryEntity
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.repositories.CategoryRepository
import com.android.splitease.repositories.GroupRepository
import com.android.splitease.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) : ViewModel() {
    val categories: StateFlow<NetworkResult<List<GetCategoryResponse>>>
        get() = categoryRepository.categories

    val findCategory: StateFlow<NetworkResult<DeleteResponse>>
        get() = categoryRepository.findCategory

    private val _category = MutableLiveData<SubCategoryEntity>()
    val category: LiveData<SubCategoryEntity> = _category

    fun fetchCategoryByName(categoryName: String) {
        viewModelScope.launch {
            _category.value = categoryRepository.getCategoryByName(categoryName)
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
        }
    }

    fun findCategory(prompt: String) {
        viewModelScope.launch {
            categoryRepository.findCategory(prompt)
        }
    }
}