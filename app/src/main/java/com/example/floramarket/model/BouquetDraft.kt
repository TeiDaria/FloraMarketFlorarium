package com.example.floramarket.model

data class BouquetDraft(
    val imageUrls: List<String> = emptyList(),
    val name: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val price: Double? = null,
    val quantity: Int? = null
)
