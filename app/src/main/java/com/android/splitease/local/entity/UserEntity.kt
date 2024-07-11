package com.android.splitease.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val uuid: String,
    val name: String,
    val email: String
)
