package com.example.floramarket

data class Flower(
    val id: String,
    val name: String,
    val price: Double,
    val mainImageUrl: String, // главное фото
    val imageUrls: List<String> = emptyList(),
    val description: String,
    val availableQuantity: Int = 1,
    val isAvailable: Boolean = true
)