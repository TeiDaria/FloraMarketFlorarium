package com.example.floramarket

data class Flower(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String, // главное фото
    val imageUrls: List<String> = emptyList(),
    val description: String,
    val isAvailable: Boolean = true
)

// Для демонстрации создадим тестовые данные
object SampleData {
    val flowers = listOf(
        Flower(
            id = "1",
            name = "Букет роз",
            price = 2500.0,
            imageUrl = "https://example.com/roses.jpg",
            description = "Красные розы, 15 штук"
        ),
        Flower(
            id = "2",
            name = "Тюльпаны",
            price = 1200.0,
            imageUrl = "https://example.com/tulips.jpg",
            description = "Желтые тюльпаны, 25 штук"
        ),
        Flower(
            id = "3",
            name = "Пионы",
            price = 3000.0,
            imageUrl = "https://example.com/peonies.jpg",
            description = "Розовые пионы, 10 штук"
        ),
        Flower(
            id = "4",
            name = "Орхидея",
            price = 1800.0,
            imageUrl = "https://example.com/orchid.jpg",
            description = "Белая орхидея в горшке"
        )
    )
}