package com.android.splitease.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subcategory",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = arrayOf("categoryId"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubCategoryEntity(
    @PrimaryKey
    val subCategoryId: Int,
    val categoryId: Int,
    val name: String,
    val imageUrl: String,
)
