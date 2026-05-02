package com.example.floramarket.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flowers")
data class Flower(
    @PrimaryKey
    val id: String,
    val name: String,
    val price: Double,
    val mainImageUrl: String, // главное фото
    val imageUrls: List<String> = emptyList(),
    val description: String = "",
    val availableQuantity: Int = 1,
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val cartQuantity: Int = 0
)