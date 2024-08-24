package com.android.splitease.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.splitease.local.entity.CategoryEntity

@Dao
interface SubCategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

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