package com.android.splitease.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.splitease.local.dao.UserDao
import com.android.splitease.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
