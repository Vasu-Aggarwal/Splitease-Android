package com.android.splitease.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    val categoryId: Int,
    val category: String,
    var lastUpdated: Long  // Timestamp in milliseconds
)

data class CategoryWithSubcategories(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val subcategories: List<SubCategoryEntity>
)
