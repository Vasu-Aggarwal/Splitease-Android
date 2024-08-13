package com.android.splitease.models.responses

data class  AddCategoryResponse(
    val subCategoryId: Int,
    val name: String,
    val imageUrl: String,
    val categoryId: String,
)