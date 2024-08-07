package com.android.splitease.models.responses

data class GetCategoryResponse(
    val category: String,
    val categoryId: Int,
    val subcategories: List<SubCategoryResponse>
)

data class SubCategoryResponse(
    val subCategoryId: Int,
    val name: String,
    val imageUrl: String,
)
