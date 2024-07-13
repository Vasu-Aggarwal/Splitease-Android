package com.android.splitease.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.splitease.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow


@Dao
public interface UserDao {
//    @Query("SELECT * FROM user")
//    suspend fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user where uuid = :uuid")
    suspend fun getUserByUuid(uuid: String): UserEntity

    @Query("SELECT * FROM user WHERE uuid = :uuid AND lastUpdated >= :minValidTimestamp")
    suspend fun getUserByUuidWithTTL(uuid: String, minValidTimestamp: Long): UserEntity

    @Query("DELETE FROM user WHERE lastUpdated <= :maxStaleTimestamp")
    suspend fun deleteStaleUsers(maxStaleTimestamp: Long)
}