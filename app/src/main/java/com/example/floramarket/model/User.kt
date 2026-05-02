package com.example.floramarket.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val avatarUrl: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isRegistered: Boolean = false
)