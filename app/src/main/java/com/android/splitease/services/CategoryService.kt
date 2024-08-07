package com.android.splitease.services

import com.android.splitease.models.responses.GetCategoryResponse
import com.android.splitease.models.responses.GetGroupsByUserResponse
import com.android.splitease.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoryService {
    @GET("${AppConstants.CATEGORY_URL}/getCategories")
    suspend fun getCategories(@Header("Authorization") token: String): Response<List<GetCategoryResponse>>

}