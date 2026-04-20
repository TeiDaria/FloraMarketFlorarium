package com.example.floramarket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateBouquetViewModel : ViewModel() {

    var imageUrls by mutableStateOf<List<String>>(emptyList())
        private set

    var name by mutableStateOf("")
        private set

    var shortDescription by mutableStateOf("")
        private set

    var fullDescription by mutableStateOf("")
        private set

    var priceInput by mutableStateOf("")
        private set

    var quantityInput by mutableStateOf("")
        private set

    val price: Double?
        get() = priceInput.toDoubleOrNull()

    val quantity: Int?
        get() = quantityInput.toIntOrNull()

    var isGeneratingName by mutableStateOf(false)
        private set

    fun updateName(newName: String){
        name = newName
    }

    fun updateShortDescription(newDesc: String) {
        shortDescription = newDesc
    }

    fun updateFullDescription(newDesc: String) {
        fullDescription = newDesc
    }

    fun updatePrice(newPrice: String){
        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d*\\.?\\d*\$"))){
            val doubleValue = newPrice.toDoubleOrNull()
            if (doubleValue == null || doubleValue <= 1_000_000.0) {
                priceInput = newPrice
            }
        }
    }

    fun updateQuantity(newQuantity: String) {
        if (newQuantity.isEmpty() || newQuantity.matches(Regex("^\\d+$"))) {
            val intValue = newQuantity.toIntOrNull()
            if (intValue==null || intValue <= 10_000){
                quantityInput = newQuantity
            }
        }
    }

    fun addImage(url: String){
        imageUrls = imageUrls + url
    }

    fun removeImage(url: String) {
        val newList = imageUrls.filter { it != url }
        imageUrls = newList
    }

    fun generateName() {
        isGeneratingName = true
        // Здесь будет запрос к ИИ
        isGeneratingName = false
    }

    fun publishBouquet(
        onSuccess: (BouquetDraft) -> Unit,
        onError: (String) -> Unit
    ) {
        when {
            imageUrls.isEmpty() -> {
                onError("Добавьте хотя бы одно фото букета")
                return
            }
            name.isBlank() -> {
                onError("Введите название букета")
                return
            }
            fullDescription.isBlank() -> {
                onError("Добавьте описание букета")
                return
            }
            price == null -> {
                onError("Укажите корректную цену")
                return
            }
            price!! > 1_000_000 -> {
                onError("Максимальная цена — 1 000 000 ₽")
                return
            }
            quantity == null -> {
                onError("Укажите количество в наличии")
                return
            }
            quantity!! > 10_000 -> {
                onError("Максимальное количество — 10 000 шт.")
                return
            }
            quantity!! <= 0 -> {
                onError("Количество должно быть больше 0")
                return
            }
        }

        val draft = BouquetDraft(
            imageUrls = imageUrls,
            name = name,
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            price = price,
            quantity = quantity
        )

        onSuccess(draft)
    }

    fun isValid(): Boolean {
        return imageUrls.isNotEmpty() &&
                name.isNotBlank() &&
                fullDescription.isNotBlank() &&
                price != null &&
                quantity != null
    }

    fun resetForm() {
        imageUrls = emptyList()
        name = ""
        shortDescription = ""
        fullDescription = ""
        priceInput = ""
        quantityInput = ""
        isGeneratingName = false
    }

}