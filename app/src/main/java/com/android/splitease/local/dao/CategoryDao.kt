package com.android.splitease.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.android.splitease.local.entity.CategoryEntity
import com.android.splitease.local.entity.CategoryWithSubcategories
import com.android.splitease.local.entity.SubCategoryEntity

@Dao
interface CategoryDao {

    @Transaction
    @Query("SELECT * FROM category")
    suspend fun getAllCategoriesWithSubcategories(): List<CategoryWithSubcategories>

    @Transaction
    suspend fun insertAllCategoriesWithSubcategories(categoriesWithSubcategories: List<CategoryWithSubcategories>) {
        // Insert categories
        val categoryEntities = categoriesWithSubcategories.map { it.category }
        insertAllCategories(categoryEntities)

        // Insert subcategories
        val subCategoryEntities = categoriesWithSubcategories.flatMap { it.subcategories }
        insertAllSubcategories(subCategoryEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubcategories(subCategories: List<SubCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategory(subCategory: SubCategoryEntity)

    @Query("DELETE FROM category")
    suspend fun deleteAll()

    @Query("SELECT * FROM category where categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Int): CategoryEntity

    @Query("SELECT * FROM category WHERE categoryId = :categoryId AND lastUpdated >= :minValidTimestamp")
    suspend fun getCategoryByIdWithTTL(categoryId: Int, minValidTimestamp: Long): CategoryEntity

    @Query("SELECT * FROM category where category = :name")
    suspend fun getCategoryByName(name: String): CategoryEntity

    @Query("SELECT * FROM category WHERE category = :name AND lastUpdated >= :minValidTimestamp")
    suspend fun getCategoryByNameWithTTL(name: String, minValidTimestamp: Long): CategoryEntity
}
