package com.android.splitease.models.responses

data class GetCategoryResponse(
    val subCategoryId: Int,
    val name: String,
    val imageUrl: String,
    val category: String,
    val categoryId: Int
)
