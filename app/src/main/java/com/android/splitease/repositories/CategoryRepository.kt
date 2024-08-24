package com.android.splitease.repositories

import android.util.Log
import com.android.splitease.di.NetworkException
import com.android.splitease.local.dao.CategoryDao
import com.android.splitease.local.entity.CategoryEntity
import com.android.splitease.local.entity.CategoryWithSubcategories
import com.android.splitease.local.entity.SubCategoryEntity
import com.android.splitease.local.entity.UserEntity
import com.android.splitease.models.responses.DeleteResponse
import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.models.responses.GetUserByUuidResponse
import com.android.splitease.models.responses.SubCategoryResponse
import com.android.splitease.services.CategoryService
import com.android.splitease.utils.AppConstants
import com.android.splitease.utils.NetworkResult
import com.android.splitease.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryService: CategoryService,
    private val tokenManager: TokenManager,
    private val categoryDao: CategoryDao){

    private val _categories = MutableStateFlow<NetworkResult<List<GetCategoryResponse>>>(
        NetworkResult.Idle())
    val categories: StateFlow<NetworkResult<List<GetCategoryResponse>>>
        get() = _categories

    private val _findCategory = MutableStateFlow<NetworkResult<DeleteResponse>>(
        NetworkResult.Idle())
    val findCategory: StateFlow<NetworkResult<DeleteResponse>>
        get() = _findCategory

    suspend fun getCategories() {
        try {
            _categories.emit(NetworkResult.Loading())

            val cachedCategories = withContext(Dispatchers.IO) {
                categoryDao.getAllCategoriesWithSubcategories()
            }

            if (cachedCategories.isNotEmpty()) {
                val responseCategories = cachedCategories.map { it.toGetCategoryResponse() }
                _categories.emit(NetworkResult.Success(responseCategories))
            } else {
                val authToken = tokenManager.getAuthToken()
                val response = categoryService.getCategories("Bearer $authToken")
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!
                    // Handle the insertion of categories and subcategories
                    withContext(Dispatchers.IO) {
                        val categoryEntities = categories.map { it.toCategoryEntity() }
                        categoryDao.insertAllCategoriesWithSubcategories(categoryEntities)
                    }
                    _categories.emit(NetworkResult.Success(categories))
                } else {
                    _categories.emit(NetworkResult.Error(response.errorBody()?.string() ?: AppConstants.UNEXPECTED_ERROR))
                }
            }
        } catch (e: NetworkException) {
            _categories.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception) {
            _categories.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun findCategory(prompt: String){
        try {
            _findCategory.emit(NetworkResult.Loading())
            val authToken = tokenManager.getAuthToken()
            val response = categoryService.findCategoryApi("Bearer $authToken", prompt)
            if (response.isSuccessful && response.body() != null) {
                _findCategory.emit(NetworkResult.Success(response.body()!!))
            } else {
                _findCategory.emit(NetworkResult.Error(response.errorBody()?.string()!!))
            }
        } catch (e: NetworkException){
            _findCategory.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        } catch (e: Exception){
            _findCategory.emit(NetworkResult.Error(e.message ?: AppConstants.UNEXPECTED_ERROR))
        }
    }

    suspend fun getCategoryByName(categoryName: String): SubCategoryEntity {
        return categoryDao.getCategoryByName(categoryName)
    }

}

fun GetCategoryResponse.toCategoryEntity(): CategoryWithSubcategories {
    val categoryEntity = CategoryEntity(
        categoryId = this.categoryId,
        category = this.category,
        lastUpdated = System.currentTimeMillis() // or use another timestamp logic if necessary
    )

    val subCategoryEntities = this.subcategories.map { subCategoryResponse ->
        SubCategoryEntity(
            subCategoryId = subCategoryResponse.subCategoryId,
            categoryId = this.categoryId,
            name = subCategoryResponse.name,
            imageUrl = subCategoryResponse.imageUrl
        )
    }

    return CategoryWithSubcategories(
        category = categoryEntity,
        subcategories = subCategoryEntities
    )
}

fun CategoryWithSubcategories.toGetCategoryResponse(): GetCategoryResponse {
    val subCategoryResponses = this.subcategories.map { subCategoryEntity ->
        SubCategoryResponse(
            subCategoryId = subCategoryEntity.subCategoryId,
            name = subCategoryEntity.name,
            imageUrl = subCategoryEntity.imageUrl // handle null imageUrl appropriately
        )
    }

    return GetCategoryResponse(
        categoryId = this.category.categoryId,
        category = this.category.category,
        subcategories = subCategoryResponses
    )
}

